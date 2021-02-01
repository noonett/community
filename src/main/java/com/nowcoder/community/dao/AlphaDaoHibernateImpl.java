package com.nowcoder.community.dao;

import com.nowcoder.community.annotation.RateLimiter;
import com.nowcoder.community.exception.RatelimiterException;
import org.springframework.stereotype.Repository;

@Repository("alphaHibernate") //可以修改bean的名字，可以通过容器强制返回对应名字的Bean
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select(){
        return "Hibernate";
    }

    @RateLimiter(key = "test", timeout = 5000, tryCount = 3, tokenPerSecond = 1)
    public String get() throws RatelimiterException {
        return "获取数据库资源";
    }
}
