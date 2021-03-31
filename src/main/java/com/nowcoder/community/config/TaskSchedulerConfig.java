package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Deprecated
@Configuration
public class TaskSchedulerConfig {
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(20);
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);//任务完成再关闭线程池
        threadPoolTaskScheduler.setAwaitTerminationSeconds(60);//设置任务等待时间,如果超过该值还没有销毁就强制销毁,确保最后关闭
        return threadPoolTaskScheduler;
    }
}


