package com.leo.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-19 09:43
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.leo.chat", "org.n3r.idworker"})
public class ChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }

}
