package service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import service.SeckillService;
import dao.SeckillDao;
import dao.SuccessKilledDao;
import dto.Exposer;
import dto.SeckillExecution;
import entity.Seckill;
import entity.SuccessKilled;
import enums.SeckillStateEnum;
import exception.RepeatKillException;
import exception.SeckillCloseException;
import exception.SeckillException;

public class SeckillServiceImpl implements SeckillService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private SeckillDao seckillDao;
	
	private SuccessKilledDao successKilledDao;
	
	//md5盐值字符串，用于混淆md5
	private final String slat = "a8v7t&*T$C*)Cvsh^oMs@adfg~dva09s8d(*&";
	
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
		Seckill seckill = seckillDao.queryById(seckillId);
		if(seckill == null) {
			return new Exposer(false, seckillId);
		}
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		//系统当前时间
		Date nowTime = new Date();
		long now = nowTime.getTime();
		if(now < startTime.getTime()
				|| now > endTime.getTime()) {
			return new Exposer(false, null, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		//转化特定字符串的过程,不可逆
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}

	private String getMD5(long sekillId) {
		String base = sekillId + "/" + slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}
	
	@Override
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException,
			RepeatKillException, SeckillCloseException {
		if(md5 == null || md5.equals(getMD5(seckillId))) {
			throw new SeckillException("sekill data rewrite");
		}
		//执行秒杀逻辑：减库存 + 记录购买行为
		Date nowTime = new Date();
		int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
		try {
			if(updateCount <= 0) {
				//没有更更新到记录，秒杀结束
				throw new SeckillCloseException("seckill is closed");
			}else {
				//记录购买行为
				int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
				//唯一：seckill, userPhone
				if(insertCount <= 0) {
					//重复秒杀
					throw new RepeatKillException("seckill repeated");
				}else {
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

}
