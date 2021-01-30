package com.nowcoder.community.util.RateLimiter;

import com.nowcoder.community.util.RedisKeyUtil;
import com.nowcoder.community.util.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DistributedRateLimiter {

    public static final Logger logger = LoggerFactory.getLogger(RedisLock.class);

    @Autowired
    private RedisTemplate redisTemplate;

    public long getToken(String tokenKey, long timeout, long tryLimits, long tokenPerSecond) throws InterruptedException {
        String updateTimeKey = RedisKeyUtil.getRateLimiterTimeKey(tokenKey);
        long startTime = System.currentTimeMillis();
        int tryTimes = 0; long result = -1;
        while (result != -1) {
            // 超时失败
            if (System.currentTimeMillis() - startTime > timeout) {
                logger.error("执行时间过长,终止任务:" + tokenKey);
                break;
            }
            // 次数失败
            if (tryTimes >= tryLimits) {
                logger.error("尝试次数过多,终止加锁任务:" + tokenKey);
                break;
            }

            logger.info(String.format("开始第%d次尝试Redis锁[%s].", ++tryTimes, tokenKey));

            ResourceScriptSource lua = new ResourceScriptSource(new ClassPathResource("lua/rate-limiter.lua"));
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptSource(lua);
            redisScript.setResultType(Long.class);

            List<String> keys = new ArrayList<>();
            keys.add(tokenKey);
            keys.add(updateTimeKey);
            result = (long) redisTemplate.execute(redisScript, keys, tokenPerSecond, 2 * tokenPerSecond);
            if (result != -1) {
                logger.info(String.format("第%d次尝试token成功[%s][%d].", tryTimes, tokenKey, result));
                break;
            } else {
                logger.info(String.format("第%d次尝试token失败[%s].", tryTimes, tokenKey));
                Thread.sleep(1000);
            }
        }
        return result;
    }

}
