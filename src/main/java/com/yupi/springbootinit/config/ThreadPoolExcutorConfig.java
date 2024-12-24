package com.yupi.springbootinit.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolExcutorConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){

        ThreadFactory threadFactory = new ThreadFactory() {
            private int count = 1;
            @Override
            public Thread newThread(@NotNull Runnable r) {

                return new Thread(r,"线程-"+count++);
            }
        };

        return new ThreadPoolExecutor(
                2,
                4,
                100,
                java.util.concurrent.TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(4),
                threadFactory,
                new java.util.concurrent.ThreadPoolExecutor.AbortPolicy()
        );
    }

}
