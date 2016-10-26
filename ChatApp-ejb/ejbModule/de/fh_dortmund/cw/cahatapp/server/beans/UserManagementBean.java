package de.fh_dortmund.cw.cahatapp.server.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Topic;

import de.fh_dortmund.inf.cw.chat.server.beans.interfaces.StatisticManagementLocal;
import de.fh_dortmund.inf.cw.chat.server.beans.interfaces.UserManagementLocal;
import de.fh_dortmund.inf.cw.chat.server.beans.interfaces.UserManagementRemote;
import de.fh_dortmund.inf.cw.chat.server.entities.User;
import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;
import de.fh_dortmund.inf.cw.chat.server.exception.LoginException;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessage;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessageType;

@Singleton
@Startup
public class UserManagementBean implements UserManagementLocal, UserManagementRemote {
	@EJB
	StatisticManagementLocal statisticManagement;
	
	@Inject
	private JMSContext jmsContext;

	@Resource(lookup = "java:global/jms/ObserverTopic")
	private Topic observerTopic;

	private LinkedHashMap<Long, User> users;
	private long lastUserNumber = 0;

	private ArrayList<User> onlineUsers;

	private boolean twiceLogin;

	@PostConstruct
	private void init() {
		users = new LinkedHashMap<Long, User>();
		onlineUsers = new ArrayList<>();
	}

	@Lock(LockType.READ)
	@Override
	public List<String> getOnlineUsers() {

		List<String> ret = new ArrayList<>();
		for (User user : onlineUsers) {
			ret.add(user.getUsername());
		}

		return ret;

	}

	@Lock(LockType.READ)
	@Override
	public int getNumberOfRegisteredUsers() {
		return (int) lastUserNumber;
	}

	@Lock(LockType.READ)
	@Override
	public int getNumbersOfOnlineUseres() {

		return onlineUsers.size();
	}

	@Lock(LockType.WRITE)
	@Override
	public void register(String name, String password) throws IllegalArgumentException {

		if (name == null) {
			throw new IllegalArgumentException("Username cannot be null");
		}
		if (password == null) {
			throw new IllegalArgumentException("Password cannot be null");
		}

		User user = new User();
		user.setUsername(name);
		user.setPassword(password);

		users.put(lastUserNumber, user);
		lastUserNumber++;

	}

	@Lock(LockType.WRITE)
	@Override
	public void changePasswort(User user, String password) {

		for (Entry<Long, User> entry : users.entrySet()) {

			if (user.getUsername().equals(entry.getValue().getUsername())) {
				entry.getValue().setPassword(password);
			}

		}

	}

	@Lock(LockType.WRITE)
	@Override
	public void login(String name, String password) throws LoginException {
		twiceLogin = false;
		for (User user : onlineUsers) {
			if (user.getPassword().equals(password)&& user.getUsername().equals(name)) {
				twiceLogin(user);
				twiceLogin=true;
				
			}
		}	
		
		for (Entry<Long, User> entry : users.entrySet()) {

			if (entry.getValue().getUsername().equals(name)) {
				if (entry.getValue().getPassword().equals(password)) {

					onlineUsers.add(entry.getValue());
					UserStatistic userStatistic = new UserStatistic();
					userStatistic.setLastLogin(new Date());
					userStatistic.setLogins(1);
					statisticManagement.updateUserStatistic(entry.getValue(), userStatistic );
					if (twiceLogin) {
						
					}else
					{
					notifyLogin(entry.getValue());
					}
					
					return;
				}
			}
		}
		throw new LoginException("Password oder Nutzername sind falsch");

	}

	@Lock(LockType.WRITE)
	@Override
	public void logout(User user) {
	
		UserStatistic userStatistic= new UserStatistic();
		userStatistic.setLogouts(1);
		for (User userOnline : onlineUsers) {
			if (user.getUsername().equals(userOnline.getUsername())) {
				onlineUsers.remove(userOnline);
					notifyLogout(userOnline);
					statisticManagement.updateUserStatistic(user, userStatistic);
				break;
			}
		}

	}

	@Lock(LockType.WRITE)
	@Override
	public void delete(User user) {
		for (User userO : onlineUsers) {
			if (user.getUsername().equals(userO.getUsername())) {
				onlineUsers.remove(userO);
				break;
			}

		}
		for (Entry<Long, User> entry : users.entrySet()) {
			if (entry.getValue().getUsername().equals(user.getUsername())) {
				users.remove(entry.getKey());
				return;
			}

		}

	}

	private void notifyLogin(User user) {
		System.out.println("login");
		ObjectMessage jmsMessage = jmsContext.createObjectMessage();
		try {
			jmsMessage.setObject(new ChatMessage(ChatMessageType.LOGIN, user.getUsername(), "login", new Date()));
			jmsContext.createProducer().send(observerTopic, jmsMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private void notifyLogout(User user) {
		System.out.println("logout");
		ObjectMessage jmsMessage = jmsContext.createObjectMessage();
		try {

			jmsMessage.setObject(new ChatMessage(ChatMessageType.LOGOUT, user.getUsername(), "logout", new Date()));
			
				
				jmsContext.createProducer().send(observerTopic, jmsMessage);
			

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void twiceLogin(User user){
		ObjectMessage jmsChatMessage = jmsContext.createObjectMessage();

		try {
			jmsChatMessage.setObject(new ChatMessage(ChatMessageType.DISCONNECT, user.getUsername(), "Twice Login", new Date()));
			jmsChatMessage.setStringProperty(ChatMessage.USER_PROPERTY_ID, user.getUsername());
			
			jmsContext.createProducer().send(observerTopic, jmsChatMessage);
		} catch (JMSException e) {
			
			e.printStackTrace();
		}

}
}