package com.fuzhou.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class SnowflakeIdUtil {
    /** 开始时间戳（UTC 2025-01-01 00:00:00），避免时间戳过长，默认可用69年 */
    private static final long START_TIMESTAMP = 1735689600000L;


    /** 数据中心ID所占位数（默认5位，支持0-31共32个数据中心） */
    private static final long DATA_CENTER_ID_BITS = 5L;

    /** 机器ID所占位数（默认5位，支持0-31共32台机器） */
    private static final long MACHINE_ID_BITS = 5L;

    /** 序列号所占位数（默认12位，每毫秒最多生成4096个ID） */
    private static final long SEQUENCE_BITS = 12L;

    // ==================== 位移计算（固定逻辑，无需修改）====================
    /** 数据中心ID的最大值（31） */
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    /** 机器ID的最大值（31） */
    private static final long MAX_MACHINE_ID = ~(-1L << MACHINE_ID_BITS);

    /** 序列号的最大值（4095） */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /** 机器ID左移位数（序列号位数） */
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;

    /** 数据中心ID左移位数（机器ID位数 + 序列号位数） */
    private static final long DATA_CENTER_ID_SHIFT = MACHINE_ID_BITS + SEQUENCE_BITS;

    /** 时间戳左移位数（数据中心ID位数 + 机器ID位数 + 序列号位数） */
    private static final long TIMESTAMP_SHIFT = DATA_CENTER_ID_BITS + MACHINE_ID_BITS + SEQUENCE_BITS;

    // ==================== 核心变量（线程安全）====================
    /** 数据中心ID（0-31） */
    private final long dataCenterId;

    /** 机器ID（0-31） */
    private final long machineId;

    /** 序列号（0-4095），原子类保证线程安全 */
    private final AtomicLong sequence = new AtomicLong(0);

    /** 上一次生成ID的时间戳（毫秒） */
    private volatile long lastTimestamp = -1L;

    // ==================== 单例实例（默认配置，也可自定义构建）====================
    /** 默认实例（数据中心ID=0，机器ID=0，适用于单机或无需区分节点的场景） */
    private static final SnowflakeIdUtil DEFAULT_INSTANCE = new SnowflakeIdUtil(0, 0);

    /**
     * 获取默认实例
     */
    public static SnowflakeIdUtil getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    /**
     * 自定义数据中心ID和机器ID，构建实例
     * @param dataCenterId 数据中心ID（0-31）
     * @param machineId    机器ID（0-31）
     */
    public static SnowflakeIdUtil getInstance(long dataCenterId, long machineId) {
        return new SnowflakeIdUtil(dataCenterId, machineId);
    }

    // ==================== 构造方法（私有，通过静态方法获取实例）====================
    private SnowflakeIdUtil(long dataCenterId, long machineId) {
        // 校验数据中心ID合法性
        if (dataCenterId < 0 || dataCenterId > MAX_DATA_CENTER_ID) {
            throw new IllegalArgumentException(String.format("数据中心ID必须在0-%d之间，当前值：%d", MAX_DATA_CENTER_ID, dataCenterId));
        }
        // 校验机器ID合法性
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException(String.format("机器ID必须在0-%d之间，当前值：%d", MAX_MACHINE_ID, machineId));
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
        log.info("雪花算法实例初始化成功：数据中心ID={}, 机器ID={}", dataCenterId, machineId);
    }

    // ==================== 核心方法：生成唯一ID ====================
    /**
     * 生成分布式唯一ID
     * @return 64位Long型唯一ID
     */
    public synchronized long generateId() {
        // 1. 获取当前时间戳（毫秒）
        long currentTimestamp = System.currentTimeMillis();

        // 2. 处理时钟回拨（当前时间戳 < 上一次时间戳）
        if (currentTimestamp < lastTimestamp) {
            long backTime = lastTimestamp - currentTimestamp;
            log.warn("时钟回拨警告：当前时间戳={}，上一次时间戳={}，回拨时长={}ms", currentTimestamp, lastTimestamp, backTime);
            // 方案1：等待时钟追上（适用于轻微回拨，如NTP同步导致）
            while (System.currentTimeMillis() < lastTimestamp) {
                // 自旋等待，直到时间戳 >= 上一次时间戳
            }
            currentTimestamp = System.currentTimeMillis();
            // 方案2：直接抛出异常（适用于严格禁止回拨的场景，注释方案1，启用下面一行）
            // throw new RuntimeException(String.format("时钟回拨异常：当前时间戳=%d < 上一次时间戳=%d", currentTimestamp, lastTimestamp));
        }

        // 3. 同一时间戳内，序列号自增
        if (currentTimestamp == lastTimestamp) {
            // 序列号自增，& MAX_SEQUENCE 保证不超过最大值（循环复用）
            sequence.compareAndSet(MAX_SEQUENCE, 0);
            long nextSequence = sequence.incrementAndGet();
            // 若序列号超过最大值（4095），说明当前毫秒内ID已耗尽，等待下一毫秒
            if (nextSequence == 0) {
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 新的时间戳，序列号重置为0
            sequence.set(0);
        }

        // 4. 更新上一次时间戳
        lastTimestamp = currentTimestamp;

        // 5. 组合ID：时间戳位移 + 数据中心ID位移 + 机器ID位移 + 序列号
        return (currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT
                | dataCenterId << DATA_CENTER_ID_SHIFT
                | machineId << MACHINE_ID_SHIFT
                | sequence.get();
    }

    // ==================== 辅助方法：等待下一毫秒 ====================
    /**
     * 等待到下一个毫秒（确保当前时间戳 > 上一次时间戳）
     * @param lastTimestamp 上一次生成ID的时间戳
     * @return 新的时间戳
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

}
