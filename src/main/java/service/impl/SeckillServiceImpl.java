package service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import service.SeckillService;
import dao.SeckillDao;
import dao.SuccessKilledDao;
import dao.cache.RedisDao;
import dto.Exposer;
import dto.SeckillExecution;
import entity.Seckill;
import entity.SuccessKilled;
import enums.SeckillStateEnum;
import exception.RepeatKillException;
import exception.SeckillCloseException;
import exception.SeckillException;

@Service
public class SeckillServiceImpl implements SeckillService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired //@Resource, @inject也可以使用，是j2ee规范的注解
	private SeckillDao seckillDao;
	
	@Autowired
	private RedisDao redisDao;
	
	@Autowired
	private SuccessKilledDao successKilledDao;
	
	//md5盐值字符串，用于混淆md5
	private final String salt = "a8v7t&*T$C*)Cvsh^oMs@adfg~dva09s8d(*&";
	
	@Override
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	@Override
	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		//优化点：缓存优化,超时的基础上维护一致性
		/**
		 * get from cache
		 * if null
		 * 	get db
		 *  else
		 *    put cache
		 * locgoin
		 */
		//1. 访问redis
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill == null) {
			//2. 访问数据库
			seckill = seckillDao.queryById(seckillId);
			if(seckill == null) {
				return new Exposer(false, seckillId);
			}else {
				//3. 放入redis
				redisDao.putSeckill(seckill);
			}
		}
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		//系统当前时间
		Date nowTime = new Date();
		long now = nowTime.getTime();
		if(now < startTime.getTime() || now > endTime.getTime()) {
			return new Exposer(false, null, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		//转化特定字符串的过程,不可逆
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}

	private String getMD5(long sekillId) {
		String base = sekillId + "/" + salt;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}
	
	@Override
	@Transactional//Spring声明式事务
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException,
			RepeatKillException, SeckillCloseException {
		if(md5 == null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("sekill data rewrite");
		}
		//执行秒杀逻辑：减库存 + 记录购买行为
		Date nowTime = new Date();
		try {
			//记录购买行为,先记录购买行为是为了减小减库存操作（行级锁）的概率
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			//唯一：seckill, userPhone
			if(insertCount <= 0) {
				//重复秒杀
				throw new RepeatKillException("seckill repeated");
			}else {
				//减库存，热点商品竞争
				int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if(updateCount <= 0) {
					//没有更更新到记录，秒杀结束
					throw new SeckillCloseException("seckill is closed");
				}else {
					//秒杀成功
					SuccessKilled succeddKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, succeddKilled);
				}
			}
			
		} catch(RepeatKillException e) {
			throw e;
		} catch(SeckillCloseException e) {
			throw e;
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			//所有编译期异常，转化为运行期异常
			throw new SeckillException("seckill inner error:"+e.getMessage());
		}
	}

	@Override
	public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
		if(md5 == null || !md5.equals(getMD5(seckillId))) {
			return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
		}
		Date killTime = new Date();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		//执行存储过程，result被复制
		try {
			seckillDao.killByProcedure(map);
			//获取result
			int result = MapUtils.getInteger(map, "result", -2);
			if(result == 1) {
				SuccessKilled sk = successKilledDao
						.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS);
			} else {
				return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
	}

}
