package com.reachauto.hkr.si.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisLock {

    public static final long INCREMENT_STEP = 1L;

    public static final int DEFAULT_EXPIRE_TIME = 3 * 60;

    public static final String LOCK_KEY_PREFIX = "hkr.si.lock.";

    @Autowired
    private RedisTemplate redisTemplate;

    public boolean lock(String lockKey) {
        String key = genKey(lockKey);
        Long val = redisTemplate.opsForValue().increment(key, INCREMENT_STEP);
        redisTemplate.expire(key, DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
        return val == INCREMENT_STEP;
    }

    public void unlock(String lockKey) {
        redisTemplate.delete(genKey(lockKey));
    }

    private static String genKey(String lockKey) {
        return LOCK_KEY_PREFIX.concat(lockKey);
    }
}
