package com.fuzhou.pojo.entity;
import lombok.Data;

/**
 * 节目-演员关联实体类
 * 对应数据库 show_actor 表（多对多中间表）
 */
@Data
public class ShowActor {
    /**
     * 主键 ID（自增）
     */
    private Long id;

    /**
     * 关联演员 ID（对应 actor 表的 id）
     */
    private Long actorId;

    /**
     * 关联节目 ID（对应 show 表的 id）
     */
    private Long showId;
}