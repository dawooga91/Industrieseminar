package de.fh_dortmund.inf.cw.chat.server.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

//Die Statistic wird erst nach 60 Minuten abgespeichert desswegen kein konkurierend Klassen

@Entity
@Table(name="COMMONSTATISTIC")
@NamedQueries(@NamedQuery( name="GET_ALL_COMMON_STATISTIC", query="Select cs  from CommonStatistic cs"))
public class CommonStatistic extends Statistic {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="startingDate")
	private Date startingDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="endDate")
	private Date endDate;
	

	public Date getStartingDate() {
		return startingDate;
	}

	public void setStartingDate(Date startDate) {
		this.startingDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
