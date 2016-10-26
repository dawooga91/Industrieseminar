package de.fh_dortmund.inf.cw.chat.server.entities;

import java.util.Date;

public class CommonStatistic extends Statistic {
	
	private Date startDate;
	private Date endDate;
	

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
