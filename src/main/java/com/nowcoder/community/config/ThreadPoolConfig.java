package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Deprecated
@Configuration
@EnableScheduling
@EnableAsync // 让@Async注解的方法在多线程环境下，被异步的调用
public class ThreadPoolConfig {
}
