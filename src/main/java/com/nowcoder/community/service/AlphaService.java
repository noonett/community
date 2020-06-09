package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prototype")            //Bean的作用域默认是单例的Singleton,多例prototype
public class AlphaService {

    @Autowired      //Service依赖Dao层实现查询
    private AlphaDao alphaDao;

    public String find(){   //模拟一个查询
        return alphaDao.select();
    }

    public AlphaService(){
        System.out.println("实例化AlphaService");
    }

    @PostConstruct    //Bean初始化，在构造函数后调用
    public void init(){
        System.out.println("初始化AlphaService");
    }

    @PreDestroy       //销毁前调用，用于释放资源
    public void destroy(){
        System.out.println("销毁AlphaService");
    }


}
