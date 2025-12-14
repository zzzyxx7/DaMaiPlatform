package com.fuzhou.common.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    // 1. 加密密码（用户注册时调用，加密后存入数据库）
    public static String encryptPassword(String rawPassword) {
        // 生成随机盐值（自带，无需手动处理），10是工作因子（越大越安全，推荐10-12）
        String salt = BCrypt.gensalt(10);
        // 密码+盐值加密，返回加密后的字符串（存到数据库password字段）
        return BCrypt.hashpw(rawPassword, salt);
    }

    // 2. 验证密码（用户登录时调用，对比输入密码和数据库加密后的密码）
    public static boolean verifyPassword(String rawPassword, String encryptedPassword) {
        // 输入密码（rawPassword）+ 数据库加密密码（encryptedPassword）对比
        return BCrypt.checkpw(rawPassword, encryptedPassword);
    }

  
}
