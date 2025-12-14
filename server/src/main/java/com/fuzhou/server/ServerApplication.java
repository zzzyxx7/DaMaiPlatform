package com.fuzhou.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 用 scanBasePackages 统一指定所有需要扫描的包，覆盖默认规则
@SpringBootApplication(scanBasePackages = {
        "com.fuzhou.server",    // 包含 server 下的所有子包（config、controller、service 等）
        "com.fuzhou.common"     // 包含 common 下的 Bean（MailUtils 等）
})
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
        System.out.println("提示：启动成功");
    }

}