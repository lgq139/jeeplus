package com.jeeplus;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAsync
@EnableWebMvc
@ServletComponentScan("com.jeeplus")
@SpringBootApplication(scanBasePackages={ "com.jeeplus"},
        exclude = {
                DruidDataSourceAutoConfigure.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        }
)
public class JeeplusWebApplication extends SpringBootServletInitializer {
    //
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 注意这里要指向原先用main方法执行的Application启动类
        return builder.sources(JeeplusWebApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(JeeplusWebApplication.class, args);
    }


}
