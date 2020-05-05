package cn.br.common.exception;

/**
 * Bean Copy Exception
 *
 * @author biezhi
 * @date 2018/4/9
 */
@SuppressWarnings("serial")
public class BeanCopyException extends RuntimeException {

	public BeanCopyException() {
	}

	public BeanCopyException(String message) {
		super(message);
	}

	public BeanCopyException(String message, Throwable cause) {
		super(message, cause);
	}

	public BeanCopyException(Throwable cause) {
		super(cause);
	}
}
