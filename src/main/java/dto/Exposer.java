package dto;
/**
 * 暴露秒杀地址DTO
 * @author WLY
 *
 */
public class Exposer {

	//是否开启秒杀
	private boolean exposed;
	
	//一种加密措施
	private String md5;
	
	private long sekillId;
	
	//系统当前时间
	private long now;
	
	private long start;
	
	private long end;

	public Exposer(boolean exposed, String md5, long sekillId) {
		super();
		this.exposed = exposed;
		this.md5 = md5;
		this.sekillId = sekillId;
	}

	public Exposer(boolean exposed, long now, long start, long end) {
		super();
		this.exposed = exposed;
		this.now = now;
		this.start = start;
		this.end = end;
	}

	public Exposer(boolean exposed, long sekillId) {
		super();
		this.exposed = exposed;
		this.sekillId = sekillId;
	}

	public Exposer(boolean exposed, String md5, long sekillId, long now, long start, long end) {
		super();
		this.exposed = exposed;
		this.md5 = md5;
		this.sekillId = sekillId;
		this.now = now;
		this.start = start;
		this.end = end;
	}
	
	
}
