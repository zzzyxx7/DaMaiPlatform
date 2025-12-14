package com.fuzhou.pojo.entity;
import lombok.Data;

/**
 * 演员实体类
 * 对应数据库 actor 表
 */
@Data
public class Actor {
    /**
     * 主键 ID（自增）
     */
    private Long id;

    /**
     * 演员姓名
     */
    private String name;

    /**
     * 人气值（默认值：0）
     */
    private Integer popularity;
}