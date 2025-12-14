package com.fuzhou.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoVO {
    private String name;
    private String account;
    private String email;
    private String image;
    private LocalDateTime createTime;
}
