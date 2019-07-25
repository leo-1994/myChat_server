package com.leo.chat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-19 09:53
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello myChat";
    }
}
