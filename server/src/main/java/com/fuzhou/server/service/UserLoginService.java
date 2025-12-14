package com.fuzhou.server.service;

import com.fuzhou.pojo.dto.UserLoginDTO;
import com.fuzhou.pojo.entity.User;
import com.fuzhou.pojo.vo.LoginVO;

public interface UserLoginService {
    LoginVO login(UserLoginDTO userLoginDTO);

    void addCode(String code, String email);

    Long verify(String email, String code);

    Boolean repeat(String account);
}
