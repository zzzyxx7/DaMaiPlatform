package com.fuzhou.server.service;

import com.fuzhou.pojo.vo.UserInfoVO;

public interface UserInfoService {

    UserInfoVO getUserInfo(Long id);

    void updateUserInfo(UserInfoVO userInfoVO, Long id);
}
