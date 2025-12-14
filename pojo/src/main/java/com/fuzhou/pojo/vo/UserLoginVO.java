package com.fuzhou.pojo.vo;

import lombok.Data;

@Data
public class UserLoginVO {
    private long id;
    private String name;
    private String account;
    private Integer status;
    private String password;
}
