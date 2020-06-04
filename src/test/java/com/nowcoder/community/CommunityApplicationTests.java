package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {
	private ApplicationContext applicationContext;

	//IOC容器会检测到set方法，将自身传进来
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/********
	 *
	 * 1. 传统的主动调用，没有使用依赖注入
	 *
 	********/

	@Test	//测试Bean的创建：Bean是如何利用面向接口编程降低调用方与实现类的耦合度的
	public void testApplicationContext() {
		System.out.println(applicationContext);							//本质是一个工厂
		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class); //面向接口，调用方和实现类耦合度下降
		System.out.println(alphaDao.select());

		//通过Bean的名字创建Bean
		alphaDao = applicationContext.getBean("alphaHibernate", AlphaDao.class);
		System.out.println(alphaDao.select());
	}

	@Test	//测试IOC管理Bean的生命周期以及作用域
	public void testBeanManagement(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);

		AlphaService alphaService2 = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService2);
	}

	@Test	//如何通过配置类引入第三方的Bean
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat =
				applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	/********
	 *
	 *   2. 使用依赖注入
	 *
 	********/

	@Autowired   //依赖注入,也可以应用在构造函数，或者在set方法
	@Qualifier("alphaHibernate")	//指定装配的Bean
	private AlphaDao alphaDao;

	@Autowired
	private AlphaService alphaService;

	@Autowired
	private SimpleDateFormat simpleDateFormat;

	@Test       //测试依赖注入
	public void testDI(){
		System.out.println(alphaDao);
		System.out.println(alphaService);
		System.out.println(simpleDateFormat);
	}
}
