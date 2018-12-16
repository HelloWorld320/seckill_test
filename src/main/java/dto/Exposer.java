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

	
	public boolean isExposed() {
		return exposed;
	}

	public void setExposed(boolean exposed) {
		this.exposed = exposed;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public long getSekillId() {
		return sekillId;
	}

	public void setSekillId(long sekillId) {
		this.sekillId = sekillId;
	}

	public long getNow() {
		return now;
	}

	public void setNow(long now) {
		this.now = now;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	@Override
	public String toString() {
		return "Exposer [exposed=" + exposed + ", md5=" + md5 + ", sekillId=" + sekillId + ", now=" + now + ", start="
				+ start + ", end=" + end + "]";
	}
	
	
}
