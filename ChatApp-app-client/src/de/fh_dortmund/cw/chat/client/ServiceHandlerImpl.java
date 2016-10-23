package de.fh_dortmund.cw.chat.client;

import java.util.List;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.fh_dortmund.inf.cw.chat.client.shared.ChatMessageHandler;
import de.fh_dortmund.inf.cw.chat.client.shared.ServiceHandler;
import de.fh_dortmund.inf.cw.chat.client.shared.UserSessionHandler;
import de.fh_dortmund.inf.cw.chat.server.beans.interfaces.PasswordHandlerRemote;
import de.fh_dortmund.inf.cw.chat.server.beans.interfaces.UserManagementRemote;
import de.fh_dortmund.inf.cw.chat.server.beans.interfaces.UserSessionManagementRemote;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessage;

public class ServiceHandlerImpl extends ServiceHandler
		implements UserSessionHandler, ChatMessageHandler, MessageListener {

	private Context ctx;
	private UserSessionManagementRemote sessionManagement;
	private UserManagementRemote userManagement;

	private static ServiceHandlerImpl instance;
	private PasswordHandlerRemote passwordHandler;
	private JMSContext jmsContext;
	private Topic observerTopic;
	private Queue chatQueue;
	private JMSConsumer chatConsumer;

	private ServiceHandlerImpl() {

		try {
			ctx = new InitialContext();
			sessionManagement = (UserSessionManagementRemote) ctx.lookup(
					"java:global/ChatApp-ear/ChatApp-ejb/UserSessionManagementBean!de.fh_dortmund.inf.cw.chat.server.beans.interfaces.UserSessionManagementRemote");
			userManagement = (UserManagementRemote) ctx.lookup(
					"java:global/ChatApp-ear/ChatApp-ejb/UserManagementBean!de.fh_dortmund.inf.cw.chat.server.beans.interfaces.UserManagementRemote");
			passwordHandler = (PasswordHandlerRemote) ctx.lookup(
					"java:global/ChatApp-ear/ChatApp-ejb/PasswordHandlerBean!de.fh_dortmund.inf.cw.chat.server.beans.interfaces.PasswordHandlerRemote");
			initilizeJMSConnection();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ServiceHandlerImpl getInstance() {
		if (instance == null) {
			instance = new ServiceHandlerImpl();
		}

		return instance;
	}

	/**
	 * Registrieren an Observer
	 */
	private void initilizeJMSConnection() {

		try {
			ConnectionFactory connectionFactory = (ConnectionFactory) ctx
					.lookup("java:comp/DefaultJMSConnectionFactory");
			jmsContext = connectionFactory.createContext();
			// Zum Empfangen
			observerTopic = (Topic) ctx.lookup("java:global/jms/ObserverTopic");
			// Zum Senden
			chatQueue = (Queue) ctx.lookup("java:global/jms/ObserverQueue");
			jmsContext.createConsumer(observerTopic).setMessageListener(this);
			unregisterConsumer();

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void changePassword(String oldPassword, String newPassword) throws Exception {
		sessionManagement.changePassword(passwordHandler.getHashPassword(oldPassword),
				passwordHandler.getHashPassword(newPassword));

	}

	@Override
	public void delete(String password) throws Exception {
		sessionManagement.delete(passwordHandler.getHashPassword(password));
	}

	@Override
	public void disconnect() {
		unregisterConsumer();
		sessionManagement.disconnect();
	}

	@Override
	public int getNumberOfOnlineUsers() {
		return userManagement.getNumbersOfOnlineUseres();
	}

	@Override
	public int getNumberOfRegisteredUsers() {
		return userManagement.getNumberOfRegisteredUsers();
	}

	@Override
	public List<String> getOnlineUsers() {
		return userManagement.getOnlineUsers();
	}

	@Override
	public String getUserName() {
		return sessionManagement.getUsername();
	}

	@Override
	public void login(String username, String password) throws Exception {
		sessionManagement.login(username, passwordHandler.getHashPassword(password));
		registerOnConsumer();
	}

	@Override
	public void logout() throws Exception {
		unregisterConsumer();
		sessionManagement.logout();
	}

	@Override
	public void register(String name, String password) throws Exception {

		System.out.println(userManagement + "---");
		userManagement.register(name, passwordHandler.getHashPassword(password));
	}

	@Override
	public void sendChatMessage(String message) {
		System.out.println("sendMessage");
		// Sendet an Observer die Nachricht
		TextMessage text = jmsContext.createTextMessage(message);

		try {
			text.setStringProperty("Name", getUserName());
			jmsContext.createProducer().send(chatQueue, text);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onMessage(Message message) {
		// Hoert auf Nachrichten vom Server
		System.out.println("onMessage");
		try {
			if (message.isBodyAssignableTo(ChatMessage.class)) {
				ChatMessage chatMessage = message.getBody(ChatMessage.class);
				setChanged();
				notifyObservers(chatMessage);
				System.out.println(chatMessage.getText());
			} else {
				System.out.println("Failed JMS Message: " + message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}
	
	private void registerOnConsumer(){
		if(chatConsumer==null){
			return;
		}else{
			String userName = getUserName();
			
			chatConsumer= jmsContext.createConsumer(observerTopic,userName);
			chatConsumer.setMessageListener(this);
		}
		
	}
	
	public void unregisterConsumer(){
		if(chatConsumer==null){
			return;
		}else
		{
			chatConsumer.close();
		}
		chatConsumer=null;
	}

}
