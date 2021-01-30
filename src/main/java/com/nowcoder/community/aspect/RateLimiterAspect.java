package com.nowcoder.community.aspect;

import com.nowcoder.community.annotation.RateLimiter;
import com.nowcoder.community.util.RateLimiter.DistributedRateLimiter;
import com.nowcoder.community.util.RedisKeyUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RateLimiterAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Autowired
    private DistributedRateLimiter distributedRateLimiter;

    @Pointcut("execution(* com.nowcoder.community.dao.*.*(..))")
    void pointcut() {
    }

    @Before("pointcut()")
    public void before(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimiter rateLimit = method.getAnnotation(RateLimiter.class);
        if (rateLimit != null) {
            String key = RedisKeyUtil.getRateLimiterKey(rateLimit.key());
            try {
                long result = distributedRateLimiter.getToken(key, rateLimit.timeout(), rateLimit.tryCount(), rateLimit.tokenPerSecond());
                if(result != -1){
                    joinPoint.proceed();
                }
            } catch (InterruptedException e) {
                logger.debug("获取" + rateLimit.key() + "的RateLimiter的token阻塞时被中断！" + e.getMessage());
            } catch (Throwable throwable) {
                logger.debug("已超过获取" + rateLimit.key() + "的RateLimiter的token次数限制！" + throwable.getMessage());
            }
        }
    }
}
