package cn.br.common.exception;

public final class UploadDownloadException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UploadDownloadException() {
	}

	public UploadDownloadException(String message) {
		super(message);
	}

	public UploadDownloadException(String message, Throwable cause) {
		super(message, cause);
	}

	public UploadDownloadException(Throwable cause) {
		super(cause);
	}

	public UploadDownloadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
