package com.nowcoder.community.service;

import com.nowcoder.community.controller.advice.ExceptionAdvice;
import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
//@Scope("prototype")            //Bean的作用域默认是单例的Singleton,多例prototype
public class AlphaService {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @Autowired      //Service依赖Dao层实现查询
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public String find() {   //模拟一个查询
        return alphaDao.select();
    }

    public AlphaService() {
        System.out.println("实例化AlphaService");
    }

    @PostConstruct    //Bean初始化，在构造函数后调用
    public void init() {
        System.out.println("初始化AlphaService");
    }

    @PreDestroy       //销毁前调用，用于释放资源
    public void destroy() {
        System.out.println("销毁AlphaService");
    }

    // 两个事务交叉的问题
    // REQUIRED：支持当前事务（外部事务：调用我的事务），如果不存在则创建新事务
    // REQUIRES_NEW：创建一个新事务，并且暂停当前事务（外部事务）
    // NESTED/；如果当前存在事务（外部事务），则嵌套在该事务中执行（独立的提交和回滚），不存在则根REQUIRED一样
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED) //传播机制
    public Object save1() {
        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("zxc3663083@163.com");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello!");
        post.setContent("新人报道");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");
        return "OK";
    }

    @Autowired
    private TransactionTemplate transactionTemplate;

    public Object save2() {
        // 新增用户
        User user = new User();
        user.setUsername("beta");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("wer3663083@163.com");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                //新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("你好!");
                post.setContent("我是新人!");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");
                return "OK";
            }
        });
    }

    // 让该方法在多线程环境下被异步调用
    @Async
    public void execute1(){
        logger.debug("execute1");

    }

    // 注解配置
//    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
//    @Async
    public void execute2(){
        logger.debug("execute2");

    }
}
