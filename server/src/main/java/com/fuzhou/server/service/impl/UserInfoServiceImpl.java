package com.fuzhou.server.service.impl;

import com.fuzhou.common.utils.RedisUtil;
import com.fuzhou.pojo.entity.User;
import com.fuzhou.pojo.vo.UserInfoVO;
import com.fuzhou.server.mapper.UserInfoMapper;
import com.fuzhou.server.service.UserInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;
    // 注入Redis工具类
    @Resource
    private RedisUtil redisUtil;

    // 定义Redis的key前缀（规范命名，避免冲突）
    private static final String REDIS_KEY_USER_INFO = "user:info:";
    // 定义过期时间（比如30分钟，可根据业务调整）
    private static final long REDIS_EXPIRE_TIME = 7200;

    @Override
    public UserInfoVO getUserInfo(Long id) {
        // 1. 拼接Redis Key：user:info:115437002433757184
        String redisKey = REDIS_KEY_USER_INFO + id;

        // 2. 先从Redis中获取数据
        System.out.println("从Redis中查询用户信息:");
        User user = redisUtil.get(redisKey, User.class);
        UserInfoVO userInfoVO = new UserInfoVO();
        // 3. Redis中不存在，再查数据库
        if (user == null) {
            System.out.println("从数据库中查询用户信息:");
            user = userInfoMapper.getUserInfo(id);

            // 4. 数据库查到数据后，存入Redis（带过期时间，避免缓存永久有效）
            if (user != null) {
                System.out.println("将用户信息存入Redis:");
                redisUtil.set(redisKey, user, REDIS_EXPIRE_TIME);
            }
        }
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }

    @Override
    public void updateUserInfo(UserInfoVO userInfoVO, Long id) {
        // 1. 前置校验：防止空指针/无效更新
        if (id == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (userInfoVO == null) {
            throw new IllegalArgumentException("更新的用户信息不能为空");
        }
        // 2. 执行数据库更新（你的原有逻辑）
        userInfoMapper.updateUserInfo(userInfoVO, id);
        log.info("用户ID:{} 的信息已更新到数据库", id);

        // 3. 核心：删除Redis中该用户的缓存（让下次查询自动加载最新数据）
        String redisKey = REDIS_KEY_USER_INFO + id;
        boolean deleteSuccess = redisUtil.delete(redisKey) > 0; // delete方法返回删除的key数量
        if (deleteSuccess) {
            log.info("用户ID:{} 的Redis缓存已删除，key:{}", id, redisKey);
        } else {
            log.warn("用户ID:{} 的Redis缓存未找到（可能未缓存），key:{}", id, redisKey);
        }
    }
}
