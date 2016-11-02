package de.fh_dortmund.inf.cw.chat.server.beans.interfaces;

import java.util.List;

import de.fh_dortmund.inf.cw.chat.server.entities.CommonStatistic;
import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;

public interface StatisticManagement {
	
	public List<CommonStatistic> getStatistics();
	
	public UserStatistic getUserStatistics(String username);
}
