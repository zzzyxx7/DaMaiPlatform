package com.fuzhou.pojo.entity;
import lombok.Data;

/**
 * 节目分类实体类
 * 对应数据库 sort 表
 */
@Data
public class Sort {
    /**
     * 主键 ID（自增）
     */
    private Long id;

    /**
     * 分类名称（如“演唱会”“话剧”）
     */
    private String name;
}