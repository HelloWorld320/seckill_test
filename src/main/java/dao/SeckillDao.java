package dao;

import java.util.Date;
import java.util.List;

import entity.Seckill;

public interface SeckillDao {

	/**
	 * 减库存
	 * @param seckillId
	 * @param killTime
	 * @return
	 */
	int reduceNumber(long seckillId, Date killTime);
	
	/**
	 * 通过id查询
	 * @param seckillId
	 * @return
	 */
	Seckill queryById(long seckillId);
	
	/**
	 * 查询全部
	 * @param offet
	 * @param limit
	 * @return
	 */
	List<Seckill> queryAll(int offet, int limit);
}
