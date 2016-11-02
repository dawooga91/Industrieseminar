package de.fh_dortmund.inf.cw.chat.server.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="USERSTATISTIC")
//@NamedQueries(@NamedQuery( name="GET_STATISTIC_BY_USER", query="select u.statistic from User u where u.username=:username "))
public class UserStatistic extends Statistic {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="lastLogin")
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