package com.nowcoder.community.aspect;

import com.nowcoder.community.annotation.RateLimiter;
import com.nowcoder.community.exception.RatelimiterException;
import com.nowcoder.community.util.RateLimiter.DistributedRateLimiter;
import com.nowcoder.community.util.RedisKeyUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
public class RateLimiterAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Autowired
    private DistributedRateLimiter distributedRateLimiter;

    @Around("execution(* com.nowcoder.community.dao.*.*(..))")
    public Object before(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimiter rateLimit = method.getAnnotation(RateLimiter.class);
        if (rateLimit != null) {
            String key = RedisKeyUtil.getRateLimiterKey(rateLimit.key());
            try {
                long result = distributedRateLimiter.getToken(key, rateLimit.timeout(), rateLimit.tryCount(), rateLimit.tokenPerSecond());
                if(result != -1){
                    return joinPoint.proceed();
                }
            } catch (InterruptedException e) {
                logger.debug("获取" + rateLimit.key() + "的RateLimiter的token阻塞时被中断！" + e.getMessage());
            }
        }else {
            return joinPoint.proceed();
        }
        String str = "获取" + rateLimit.key() + "的token失败！";
        logger.debug(str);
        throw new RatelimiterException(str);
    }
}
