package com.fuzhou.pojo.entity;
import lombok.Data;

/**
 * 节目实体类
 * 对应数据库 show 表
 */
@Data
public class Show {
    /**
     * 主键 ID（自增）
     */
    private Long id;

    /**
     * 节目名称
     */
    private String title;

    /**
     * 关联分类 ID（对应 sort 表的 id）
     */
    private Long sortId;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 演出场馆名称
     */
    private String venueName;

    /**
     * 场馆详细地址（门牌号等）
     */
    private String detailAddress;

    /**
     * 完整地址（冗余字段）
     */
    private String fullAddress;
}