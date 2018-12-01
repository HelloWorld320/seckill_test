package dao;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import entity.Seckill;

/**
 * 配置Spring和Junit整合，Junit启动时加载spring IOC容器
 * @author WLY
 * spring-test, junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉Junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

	//注入dao实现类依赖
	@Resource
	private SeckillDao seckillDao;
	
	@Test
	public void testReduceNumber() {
		Date killTime = new Date();
		int updateCount = seckillDao.reduceNumber(1000L, killTime);
		System.out.println(updateCount);
	}

	@Test
	public void testQueryById() {
		/*
		 * 	org.mybatis.spring.MyBatisSystemException: nested exception is org.apache.ibatis.binding.BindingException: Parameter 'offset' not found. Available parameters are [1, 0, param1, param2]
		 *  List<Seckill> queryAll(int offet, int limit);
		 *  java在运行时没有保存形参的名称：List<Seckill> queryAll(int offet, int limit);
		 *  -》List<Seckill> queryAll(arg0, arg1);
		 *  当dao方法有多个参数时，MyBatis无法分辨参数的对应关系，如果只有一个参数则没有这种问题
		 *  可以使用MyBatis的@Param注解解决这种问题
		 */
		long id = 1000;
		Seckill seckill = seckillDao.queryById(id);
		System.out.println(seckill);
	}

	@Test
	public void testQueryAll() {
		List<Seckill> seckills = seckillDao.queryAll(0, 100);
		for(Seckill seckill : seckills) {
			System.out.println(seckill);
		}
	}

}
