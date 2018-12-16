-- 数据库初始化脚本
-- 创建数据库
create database seckill;
-- 使用数据库
use seckill;

-- 创建秒杀库存表
-- MySql5.5版本中：如果有两个timestamp字段，但是只把第一个设定为current_timestamp而第二个没有设定默认值，mysql也能成功建表,但是反过来就不行...
create table seckill (
	seckill_id bigint not null auto_increment comment '商品库存id',
	name varchar(120) not null comment '商品名称',
	number int not null comment '库存数量',
	create_time timestamp not null default current_timestamp comment '创建时间',
	start_time timestamp not null comment '秒杀开启时间',
	end_time timestamp not null comment '秒杀结束时间',
	primary key (seckill_id),
	key idx_start_time (start_time),
	key idx_end_time (end_time),
	key idx_create_time (create_time)
)engine=InnoDB auto_increment=1000 default charset=utf8 comment='秒杀库存表';

-- 初始化数据
insert into
	seckill(name, number, start_time, end_time)
values
	('1000元秒杀iPhone8', 100, '2018-11-21 00:00:00', '2018-11-22 00:00:00'),
	('500元秒杀ipad3', 200, '2018-11-21 00:00:00', '2018-11-22 00:00:00'),
	('300元秒杀小米5', 300, '2018-11-21 00:00:00', '2018-11-22 00:00:00'),
	('100元秒杀红米4', 400, '2018-11-21 00:00:00', '2018-11-22 00:00:00');
	
-- 秒杀成功明细表
-- 用户登录认证相关信息
create table success_killed(
	seckill_id bigint not null comment '秒杀商品id',
	user_phone bigint not null comment '用户手机号',
	state tinyint not null default -1 comment '状态标识：-1：无效，0：成功，1：已付款，2：已发货',
	create_time timestamp not null comment '创建时间',
	primary key (seckill_id, user_phone), /* 联合主键 */
	key idx_create_time(create_time)
)engine=InnoDB default charset=utf8 comment='秒杀库存表';