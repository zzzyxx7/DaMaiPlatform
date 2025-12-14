package com.fuzhou.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserLoginDTO {
    private Long id;
    /**
     * 用户名（默认值：用户未命名）
     */
    private String name;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱（默认值：空字符串）
     */
    private String email;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private String image;

    private String code;
    private Integer type;
}
