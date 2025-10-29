package com.cy.crm;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 客户关系管理服务应用主类
 */
@SpringBootApplication
public class CrmServiceApplication {

    public static void main(String[] args) {
        // 加载 .env 文件（默认在项目根目录）
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() // 如果 .env 不存在也不报错
                .load();

        // 将 .env 中的变量设置为系统属性（这样 Spring 就能读取）
        dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));


        SpringApplication.run(CrmServiceApplication.class, args);
    }

}