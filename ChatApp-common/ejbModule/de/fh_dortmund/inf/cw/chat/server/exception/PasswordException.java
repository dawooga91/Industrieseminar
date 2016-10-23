package de.fh_dortmund.inf.cw.chat.server.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class PasswordException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PasswordException() {
		super();
	}

	public PasswordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PasswordException(String message, Throwable cause) {
		super(message, cause);
	}

	public PasswordException(String message) {
		super(message);
	}

	public PasswordException(Throwable cause) {
		super(cause);
	}
	
	
	

}
