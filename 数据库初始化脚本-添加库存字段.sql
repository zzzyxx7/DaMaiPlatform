-- ============================================
-- 管理端演出管理模块 - 数据库初始化脚本
-- ============================================
-- 说明：给 sessions 表添加库存字段
-- 执行时间：2025-01-XX
-- ============================================

-- 1. 给 sessions 表添加库存字段
ALTER TABLE sessions 
ADD COLUMN stock INT DEFAULT 0 COMMENT '剩余库存（可售票数）',
ADD COLUMN total_stock INT DEFAULT 0 COMMENT '初始库存（总票数）';

-- 2. 初始化现有数据的库存（假设每个场次1000张票，你可以根据实际情况修改）
-- 注意：请根据实际业务需求修改库存数量
UPDATE sessions 
SET stock = 1000, total_stock = 1000 
WHERE stock IS NULL OR stock = 0;

-- 3. 验证数据（可选，检查是否添加成功）
-- SELECT id, show_id, start_time, stock, total_stock FROM sessions LIMIT 10;

-- ============================================
-- 说明：
-- 1. stock：剩余库存，下单时会扣减
-- 2. total_stock：初始库存，用于计算已售数量（已售 = total_stock - stock）
-- 3. 如果不需要初始库存，可以只保留 stock 字段
-- ============================================


















