package com.fuzhou.pojo.vo;

import lombok.Data;

@Data
public class LoginVO {
    private String access_token;
    private String refresh_token;
    private String msg;
}
