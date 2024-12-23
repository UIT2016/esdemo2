package com.city.esdemo2.controller;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Slf4j
public class HelloController {
    @GetMapping("hello")
    public String hello(){
        log.info("=========测试filebeat");
        log.info("=========test filebeat");
        return "hello,world!";
    }
    @GetMapping("hello2")
    public String throwError(){
        log.warn("测试异常收集");
        log.error("test filebeat");
        throw new RuntimeException("测试异常日志");

    }

}
