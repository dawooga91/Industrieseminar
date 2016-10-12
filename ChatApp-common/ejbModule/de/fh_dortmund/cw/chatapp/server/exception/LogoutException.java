package de.fh_dortmund.cw.chatapp.server.exception;

public class LogoutException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LogoutException() {
		super();
	}

	public LogoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LogoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public LogoutException(String message) {
		super(message);
	}

	public LogoutException(Throwable cause) {
		super(cause);
	}
	

}
