package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
public class RedisLock {

    public static final Logger logger = LoggerFactory.getLogger(RedisLock.class);

    @Autowired
    private RedisTemplate redisTemplate;

    // 获取锁超时时间
    @Value("${redisLock.timeout}")
    private long timeout;

    // 获取锁失败后阻塞时间
    @Value("${redisLock.waitPeriod}")
    private long waitPeriod;

    // 尝试次数
    @Value("${redisLock.tryLimits}")
    private long tryLimits;

    /**
     * @param expireTime 单位为秒
     */
    public boolean lock(String key, String token,
                        int expireTime) throws InterruptedException {
        boolean ret = false;
        long startTime = System.currentTimeMillis();
        int tryTimes = 0;
        while (!ret) {
            // 超时失败
            if (System.currentTimeMillis() - startTime > timeout) {
                logger.error("执行时间过长,终止任务:" + token);
                break;
            }
            // 次数失败
            if (tryTimes >= tryLimits) {
                logger.error("尝试次数过多,终止加锁任务:" + token);
                break;
            }
            logger.info(String.format("开始第%d次尝试Redis锁[%s].", ++tryTimes, token));
            ret = redisTemplate.opsForValue().setIfAbsent(key, token, Duration.ofSeconds(expireTime));
            if (ret) {
                logger.info(String.format("第%d次尝试Redis锁成功[%s].", tryTimes, token));
                break;
            } else {
                logger.info(String.format("第%d次尝试Redis锁失败[%s].", tryTimes, token));
                Thread.sleep(waitPeriod);
            }
        }
        return ret;
    }

    public boolean unLock(String key, String token) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);

        Object result = redisTemplate.execute(redisScript, Collections.singletonList(key), token);
        Long success = 1L;
        if (success.equals(result)) {
            logger.info("解锁成功！-" + result + "-token:" + token);
            return true;
        }
        logger.info("解锁失败！" + result + "-token:" + token);
        return false;
    }

}
