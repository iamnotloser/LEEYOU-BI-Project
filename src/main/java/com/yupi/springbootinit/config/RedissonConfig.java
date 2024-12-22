package com.yupi.springbootinit.config;

import lombok.Data;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Data
@ConfigurationProperties(prefix = "spring.redis")
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient getRedisson() {
        Config config = new Config();
        config.useSingleServer()
                // use "rediss://" for SSL connection
                .setAddress("redis://127.0.0.1:6379")
                .setDatabase(3);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

}
