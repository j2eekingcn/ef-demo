package cn.br.common.exception;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The super class for all exceptions
 */
public abstract class EfException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	static AtomicLong atomicLong = new AtomicLong(System.currentTimeMillis());
	String id;

	void setId() {
		long nid = atomicLong.incrementAndGet();
		id = Long.toString(nid, 26);
	}

	public EfException() {
		setId();
	}

	public EfException(String message) {
		super(message);
		setId();
	}

	public EfException(String message, Throwable cause) {
		super(message, cause);
		setId();
	}

	public abstract String getErrorTitle();

	public abstract String getErrorDescription();

	public String getId() {
		return id;
	}

}