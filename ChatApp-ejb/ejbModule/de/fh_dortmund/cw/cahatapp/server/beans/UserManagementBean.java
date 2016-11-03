package de.fh_dortmund.cw.cahatapp.server.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import de.fh_dortmund.inf.cw.chat.server.beans.interfaces.StatisticManagementLocal;
import de.fh_dortmund.inf.cw.chat.server.beans.interfaces.UserManagementLocal;
import de.fh_dortmund.inf.cw.chat.server.beans.interfaces.UserManagementRemote;
import de.fh_dortmund.inf.cw.chat.server.entities.User;
import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;
import de.fh_dortmund.inf.cw.chat.server.exception.LoginException;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessage;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessageType;

@Stateless
public class UserManagementBean implements UserManagementLocal, UserManagementRemote {
	@EJB
	StatisticManagementLocal statisticManagement;

	@Inject
	private JMSContext jmsContext;

	@Resource(lookup = "java:global/jms/ObserverTopic")
	private Topic observerTopic;



	@PersistenceContext(unitName = "ChatDB")
	private EntityManager entityManager;

	// private LinkedHashMap<Long, User> users;
	// private long lastUserNumber = 0;

	private ArrayList<User> onlineUsers;

	private boolean twiceLogin;


	//private static final String GET_USER_BY_UUID= "Select u from User where :uuid=uuid";


	@PostConstruct
	private void init() {
		// users = new LinkedHashMap<Long, User>();
		onlineUsers = new ArrayList<>();
	}

	@Override
	public List<String> getOnlineUsers() {

		List<String> ret = new ArrayList<>();
		for (User user : onlineUsers) {
			ret.add(user.getUsername());
		}

		return ret;

	}

	@Override
	public int getNumberOfRegisteredUsers() {
		TypedQuery<Long> query = entityManager.createNamedQuery("GET_USER_COUNT",Long.class);
		long singleResult = query.getSingleResult();
	
		return (int)singleResult;
		
	}

	@Override
	public int getNumbersOfOnlineUseres() {

		return onlineUsers.size();
	}

	//
	// @Lock(LockType.WRITE)
	@Override
	public void register(String name, String password) throws IllegalArgumentException {

		if (name == null) {
			throw new IllegalArgumentException("Username cannot be null");
		}
		if (password == null) {
			throw new IllegalArgumentException("Password cannot be null");
		}
		TypedQuery<User> query = entityManager.createNamedQuery("GET_USER_BY_NAME",User.class);
		query.setParameter("username", name);
		
		try {
			User result = query.getSingleResult();
			if (result==null) {
				throw new IllegalArgumentException("Username vergeben");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		User user = new User();
		UserStatistic userStatistic = new UserStatistic();
		entityManager.persist(userStatistic);
		
		user.setStatistic(userStatistic);
		user.setUsername(name);
		user.setPassword(password);
		entityManager.persist(user);

		// users.put(lastUserNumber, user);
		// lastUserNumber++;

	}

	// @Lock(LockType.WRITE)
	@Override
	public void changePasswort(User user, String password) {

		user.setPassword(password);

		entityManager.merge(user);

	}

//	@Lock(LockType.WRITE)
	@Override
	public void login(String name, String password) throws LoginException {
		//Starte Timer wenn erster User
		statisticManagement.startTimer();
		
//		Nutzername und Passwort 
		TypedQuery<User> query = entityManager.createNamedQuery("GET_USER_BY_NAME", User.class);
		query.setParameter("username", name);
		User user=null;
		try {
			
			user = query.getSingleResult();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		System.out.println("Passwort Test: " + user.getPassword().equals(password));
		if (user == null|| !(user.getPassword().equals(password)))
			throw new LoginException("Password oder Nutzername sind falsch");
		
		twiceLogin = false;
		for (User userTemp : onlineUsers) {
			if (userTemp.getOid()==user.getOid()) {
				twiceLogin(userTemp);
				twiceLogin = true;

			}
		}
		
		
		
		//User Online
		if(twiceLogin){
			twiceLogin(user);
		}
		
		
		onlineUsers.add(user);
		
		notifyLogin(user);
		
		
		//Statistic
		UserStatistic userStatistic = new UserStatistic();
		userStatistic.setLastLogin(new Date());
		userStatistic.setLogins(1);
		statisticManagement.updateUserStatistic(user, userStatistic);

	}

//	@Lock(LockType.WRITE)
	@Override
	public void logout(User user) {

		UserStatistic userStatistic = new UserStatistic();
		userStatistic.setLogouts(1);
		
		//entityManager.createNamedQuery(GET_USER_BY_UUID);
		
		for (User userOnline : onlineUsers) {
			if (user.getOid()==user.getOid()) {
				onlineUsers.remove(userOnline);
				notifyLogout(userOnline);
				statisticManagement.updateUserStatistic(user, userStatistic);
				break;
			}
		}

	}

//	@Lock(LockType.WRITE)
	@Override
	public void delete(User user) {

		entityManager.remove(user);

		// for (Entry<Long, User> entry : users.entrySet()) {
		// if (entry.getValue().getUsername().equals(user.getUsername())) {
		// users.remove(entry.getKey());
		// return;
		// }
		//
		// }

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

	public void twiceLogin(User user) {
		ObjectMessage jmsChatMessage = jmsContext.createObjectMessage();

		try {
			jmsChatMessage.setObject(
					new ChatMessage(ChatMessageType.DISCONNECT, user.getUsername(), "Twice Login", new Date()));
			jmsChatMessage.setStringProperty(ChatMessage.USER_PROPERTY_ID, user.getUsername());

			jmsContext.createProducer().send(observerTopic, jmsChatMessage);
		} catch (JMSException e) {

			e.printStackTrace();
		}

	}
}