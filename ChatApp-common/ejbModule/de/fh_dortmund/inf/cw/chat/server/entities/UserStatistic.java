package de.fh_dortmund.inf.cw.chat.server.entities;

import java.util.Date;

public class UserStatistic extends Statistic {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date lastLogin;

	public UserStatistic() {
		super();
	}

	public Date getLastLogin() {

		return lastLogin;

	}

	public void setLastLogin(Date lastLogin) {

		this.lastLogin = lastLogin;

	}
}
