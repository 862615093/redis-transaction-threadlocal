package com.ww;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisTransactionThreadLocal {
    public static void main(String[] args) {
        SpringApplication.run(RedisTransactionThreadLocal.class, args);
    }
}