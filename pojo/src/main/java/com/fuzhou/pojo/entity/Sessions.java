package com.fuzhou.pojo.entity;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.Duration;

/**
 * 节目场次实体类
 * 对应数据库 sessions 表
 */
@Data
public class Sessions {
    /**
     * 主键 ID（自增）
     */
    private Long id;

    /**
     * 关联节目 ID（对应 show 表的 id）
     */
    private Long showId;

    /**
     * 场次开始时间
     */
    private LocalDateTime startTime;

    /**
     * 场次持续时间
     */
    private Duration duration;
}