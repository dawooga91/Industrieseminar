package de.fh_dortmund.inf.cw.chat.server.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="Chatuser")
@NamedQueries({
	@NamedQuery(name="GET_USER_BY_NAME", query="Select u from User u where u.username=:username"),
	@NamedQuery(name="GET_USER_BY_NAME_PASSWORD", query= "Select u from User u where u.password=:password AND u.username=:username"),
	@NamedQuery(name = "GET_USER_COUNT", query = "select count(u) from User u"),
})
public class User extends BaseEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	@Basic(optional=false)
	@Column(nullable= false)
	private String username;
	
	@Basic(optional=false)
	@Column(nullable= false)
	private String password;

	
	@JoinColumn(name="statistic")
	@OneToOne(cascade={CascadeType.PERSIST,CascadeType.REMOVE})
	private UserStatistic statistic;
	
	public User() {
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	

	public UserStatistic getStatistic() {
		return statistic;
	}

	public void setStatistic(UserStatistic statistic) {
		this.statistic = statistic;
	}

	




	

	

}
