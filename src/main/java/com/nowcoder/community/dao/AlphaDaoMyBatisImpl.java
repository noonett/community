package com.nowcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary   //同一个接口实现的，优先装配这个Bean。这样的话调用方不用修改他们的代码，因为调用方依赖的是接口
public class AlphaDaoMyBatisImpl implements AlphaDao{
    @Override
    public String select() {
        return "MyBatis";
    }
}
