package de.fh_dortmund.inf.cw.chat.server.beans.interfaces;

import javax.ejb.Local;

import de.fh_dortmund.inf.cw.chat.server.entities.User;
import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;

@Local
public interface StatisticManagementLocal extends StatisticManagement {

	public void updateUserStatistic(User user, UserStatistic userStatistic);
	public void startTimer();
}
