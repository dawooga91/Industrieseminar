package de.fh_dortmund.cw.cahatapp.server.beans;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import de.fh_dortmund.inf.cw.chat.server.beans.interfaces.StatisticManagementLocal;
import de.fh_dortmund.inf.cw.chat.server.beans.interfaces.StatisticManagementRemote;
import de.fh_dortmund.inf.cw.chat.server.entities.CommonStatistic;
import de.fh_dortmund.inf.cw.chat.server.entities.User;
import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;

@Singleton
@Startup
public class UserStatisticBean implements StatisticManagementRemote, StatisticManagementLocal {

	private final String USER_STATISTIC_TIMER= "USER_STATISTIC_TIMER";
	
	@Resource
	private TimerService timerService;

	private HashMap<String, UserStatistic> userStatistics;
	
	
	@PostConstruct
	private void init(){
		
		userStatistics = new HashMap<>();
		
		boolean createTimer=true;
		
		//Check for Timers
		for (Timer timer : timerService.getTimers()) {
			if(USER_STATISTIC_TIMER.equals(timer.getInfo())){
				createTimer=false;
				break;
			}
			
		}
		
		if (!createTimer) {
			//Common
			TimerConfig timerConfig= new TimerConfig();
			timerConfig.setInfo(USER_STATISTIC_TIMER);
			timerConfig.setPersistent(true);
			
			//Interval Timer
			GregorianCalendar initialExperiatianCalendar = new GregorianCalendar();
			initialExperiatianCalendar.set(Calendar.HOUR_OF_DAY, 0);
			initialExperiatianCalendar.set(Calendar.MINUTE,0);
			initialExperiatianCalendar.set(Calendar.SECOND, 0);
			initialExperiatianCalendar.set(Calendar.DAY_OF_MONTH, 1);
			
			timerService.createIntervalTimer(initialExperiatianCalendar.getTime(), 108000000, timerConfig);
			
		}
		
	}
	@Timeout
	public void timeout(Timer timer) {
		if (USER_STATISTIC_TIMER.equals(timer.getInfo())) {
			GregorianCalendar currentDateCalendar = new GregorianCalendar();
			currentDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
			currentDateCalendar.set(Calendar.MINUTE,0);
			currentDateCalendar.set(Calendar.SECOND, 0);
			currentDateCalendar.set(Calendar.MILLISECOND, 0);
			
			GregorianCalendar startingDateCalendar = new GregorianCalendar();
			startingDateCalendar.setTime(currentDateCalendar.getTime());
			startingDateCalendar.add(Calendar.DAY_OF_MONTH, -1);
			 Date time = startingDateCalendar.getTime();
			 
			 GregorianCalendar endDateCalendar = new GregorianCalendar();
			 endDateCalendar.setTime(currentDateCalendar.getTime());
			 endDateCalendar.add(Calendar.MILLISECOND, -1);
				 Date endTime = endDateCalendar.getTime();
			
		}
	}
	@Override
	public List<CommonStatistic> getStatistics() {
		
		return null;
	}
	@Override
	public UserStatistic getUserStatistics(String username) {
		
		return userStatistics.get(username);
	}
	@Override
	public void updateUserStatistic(User user, UserStatistic userStatistic) {
		UserStatistic oldStatistic = userStatistics.get(user.getUsername());
		if(oldStatistic!=null){
			oldStatistic.setLogouts(oldStatistic.getLogouts()+userStatistic.getLogouts());
			oldStatistic.setMessages(oldStatistic.getMessages()+userStatistic.getMessages());
			if(userStatistic.getLogins()!=0){
				oldStatistic.setLogins(oldStatistic.getLogins()+userStatistic.getLogins());
				oldStatistic.setLastLogin(userStatistic.getLastLogin());
			}
			
		} 
		else{
			userStatistics.put(user.getUsername(), userStatistic);
		}
			
		
		
	}
	@Override
	public void updateCommonStatistic(CommonStatistic commonStatistic) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
