package com.fuzhou.server.mapper;

import com.fuzhou.pojo.entity.User;
import com.fuzhou.pojo.vo.UserInfoVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper {
    User getUserInfo(Long id);

    void updateUserInfo(UserInfoVO userInfoVO, Long id);

    String selectCityByUserId(Long loginUserId);
}
