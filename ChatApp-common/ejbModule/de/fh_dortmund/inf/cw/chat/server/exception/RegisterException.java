package de.fh_dortmund.inf.cw.chat.server.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class RegisterException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RegisterException() {
		super();
	}

	public RegisterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RegisterException(String message, Throwable cause) {
		super(message, cause);
	}

	public RegisterException(String message) {
		super(message);
	}

	public RegisterException(Throwable cause) {
		super(cause);
	}
	
	
	
}
