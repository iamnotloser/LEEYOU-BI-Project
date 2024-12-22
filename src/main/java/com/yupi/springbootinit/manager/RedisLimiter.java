package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 专门提供限流服务
 */
@Service
public class RedisLimiter {
    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流操作
     * @param key
     */
    public void doRateLimit(String key)  {

        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);

        boolean allowed =rateLimiter.tryAcquire(1);
        if(!allowed) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
