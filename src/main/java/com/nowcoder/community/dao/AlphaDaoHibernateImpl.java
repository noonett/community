package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphaHibernate") //可以修改bean的名字，可以通过容器强制返回对应名字的Bean
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select(){
        return "Hibernate";
    }
}
