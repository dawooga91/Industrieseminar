package de.fh_dortmund.cw.cahatapp.server.beans;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import de.fh_dortmund.inf.cw.chat.server.beans.interfaces.StatisticManagementLocal;
import de.fh_dortmund.inf.cw.chat.server.beans.interfaces.StatisticManagementRemote;
import de.fh_dortmund.inf.cw.chat.server.entities.CommonStatistic;
import de.fh_dortmund.inf.cw.chat.server.entities.User;
import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessage;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessageType;

@Stateless
public class UserStatisticBean implements StatisticManagementRemote, StatisticManagementLocal {

	private static final int ServerTime = 60 * 1000 * 60;
	private final String USER_FULL_STATISTIC_TIMER = "USER_FULL_STATISTIC_TIMER";
	private final String USER_HALF_STATISTIC_TIMER = "USER_STATISTIC_TIMER";
	@Inject
	private JMSContext jmsContext;

	@Resource(lookup = "java:global/jms/ObserverTopic")
	private Topic observerTopic;

	@Resource
	private TimerService timerService;


	private CommonStatistic commonStatistic;
	private boolean createHalfTimer;

	@PersistenceContext(unitName = "ChatDB")
	private EntityManager entityManager;
	

	@PostConstruct
	private void init() {
		commonStatistic = new CommonStatistic();
		commonStatistic.setStartingDate(new Date());
		// commonStatistics.add(commonStatistic);

		// createFullTimer=true;
		// // Check for Timers
		// for (Timer timer : timerService.getTimers()) {
		// if (USER_FULL_STATISTIC_TIMER.equals(timer.getInfo())) {
		// createFullTimer = false;
		// break;
		// }
		//
		// }
		// if (createFullTimer) {
		// // Common
		// TimerConfig timerConfig = new TimerConfig();
		// timerConfig.setInfo(USER_FULL_STATISTIC_TIMER);
		// timerConfig.setPersistent(false);
		//
		//
		//
		// //CalenderTimer
		// ScheduleExpression scheduleExpression = new ScheduleExpression();
		// scheduleExpression.year("*");
		// scheduleExpression.month("*");
		// scheduleExpression.dayOfMonth("*");
		// scheduleExpression.dayOfWeek("*");
		// scheduleExpression.hour("*");
		// scheduleExpression.minute(0);
		// scheduleExpression.second(0);
		//
		//
		//
		// timerService.createCalendarTimer(scheduleExpression,timerConfig);
		//

	}

	@Override
	public void startTimer() {

		createHalfTimer = true;

		// Check for Timers
		for (Timer timer : timerService.getTimers()) {
			if (USER_HALF_STATISTIC_TIMER.equals(timer.getInfo())) {
				createHalfTimer = false;
				break;
			}

		}

		if (createHalfTimer) {
			// Common
			TimerConfig timerConfig = new TimerConfig();
			timerConfig.setInfo(USER_HALF_STATISTIC_TIMER);
			timerConfig.setPersistent(false);

			// Interval Timer
			GregorianCalendar initialExperiatianCalendar = new GregorianCalendar();
			// timer geht um halb los
			initialExperiatianCalendar.set(Calendar.MINUTE, 30);
			initialExperiatianCalendar.set(Calendar.SECOND, 0);
			initialExperiatianCalendar.set(Calendar.MILLISECOND, 0);

			// ScheduleExpression scheduleExpression = new ScheduleExpression();
			// scheduleExpression.minute(30);
			// scheduleExpression.second(0);

			timerService.createIntervalTimer(initialExperiatianCalendar.getTime(), ServerTime, timerConfig);

		}

	}

	@Schedule(info = USER_FULL_STATISTIC_TIMER, persistent = false, minute = "*", hour = "*")
	public void timeOutSchedule() {
		System.out.println("test");

		commonStatistic.setEndDate(new Date());
		entityManager.persist(commonStatistic);
		// commonStatistics.add(commonStatistic);
		notifyStatistic("vollen");
		commonStatistic = new CommonStatistic();
		commonStatistic.setStartingDate(new Date());

	}

	@Lock(LockType.READ)
	@Timeout
	public void timeout(Timer timer) {

		if (USER_HALF_STATISTIC_TIMER.equals(timer.getInfo())) {

			if (commonStatistic != null) {

				notifyStatistic("halben");
			}

			if (USER_HALF_STATISTIC_TIMER.equals(timer.getInfo())) {

			}
		}
	}

	private void notifyStatistic(String zeit) {
		ObjectMessage jmsMessage = jmsContext.createObjectMessage();
		try {
			Date date = new Date();
			String text = "Statistik der letzten " + zeit + " Stunde \n Anzahl der Anmeldungen: "
					+ commonStatistic.getLogins() + "\n Anzahl der Abmeldungen: " + commonStatistic.getLogouts()
					+ "\nAnzahl der Nachrichten " + commonStatistic.getMessages() + "\n\n\n";
			jmsMessage.setObject(new ChatMessage(ChatMessageType.STATISTIC, "Server", text, date));
			jmsContext.createProducer().send(observerTopic, jmsMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<CommonStatistic> getStatistics() {

		//ToDo
		TypedQuery<CommonStatistic> query = entityManager.createNamedQuery("GET_ALL_COMMON_STATISTIC",
				CommonStatistic.class);

		return query.getResultList();
	}

	@Override
	public UserStatistic getUserStatistics(String username) {
		
		TypedQuery<User> queryUser = entityManager.createNamedQuery("GET_USER_BY_NAME",User.class);
		queryUser.setParameter("username", username);
		User user = queryUser.getSingleResult();
		
		 TypedQuery<UserStatistic> query = entityManager.createNamedQuery("GET_STATISTIC_BY_USER", UserStatistic.class);
		 
		 query.setParameter("user", user);
		 UserStatistic result = query.getSingleResult();
		 return result;
		 
		 
		 
	}

	@Override
	public void updateUserStatistic(User user, UserStatistic userStatistic) {
		
		UserStatistic oldStatistic = user.getStatistic();
		
		
		if (oldStatistic != null) {
			oldStatistic.setLogouts(oldStatistic.getLogouts() + userStatistic.getLogouts());
			oldStatistic.setMessages(oldStatistic.getMessages() + userStatistic.getMessages());
			if (userStatistic.getLogins() != 0) {
				oldStatistic.setLogins(oldStatistic.getLogins() + userStatistic.getLogins());
				oldStatistic.setLastLogin(userStatistic.getLastLogin());
				entityManager.merge(oldStatistic);
			}

		} else {
			entityManager.persist(userStatistic);
		}
		
		//CommonStatistic
		CommonStatistic newStatistic = new CommonStatistic();
		newStatistic.setLogins(userStatistic.getLogins());
		newStatistic.setLogouts(userStatistic.getLogouts());
		newStatistic.setMessages(userStatistic.getMessages());
		updateCommonStatistic(newStatistic);
	}

	public void updateCommonStatistic(CommonStatistic newStatistic) {

		System.out.println(commonStatistic.getLogins() + "\n" + commonStatistic.getMessages());
		// for (CommonStatistic commonStatistic : commonStatistics) {
		// if (commonStatistic.getEndDate()==null) {
		commonStatistic.setLogins(commonStatistic.getLogins() + newStatistic.getLogins());
		commonStatistic.setMessages(commonStatistic.getMessages() + newStatistic.getMessages());
		commonStatistic.setLogouts(commonStatistic.getLogouts() + newStatistic.getLogouts());
		// }
		// }

	}

}
