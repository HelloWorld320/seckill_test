package exception;

/**
 * 重复秒杀异常（运行期异常）,spring声明式事务只接受运行期异常
 * @author WLY
 */
public class RepeatKillException extends SeckillException {

	public RepeatKillException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public RepeatKillException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
}
