package de.fh_dortmund.cw.cahatapp.server.beans;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.jms.Topic;

import de.fh_dortmund.inf.cw.chat.server.beans.interfaces.StatisticManagementLocal;
import de.fh_dortmund.inf.cw.chat.server.entities.User;
import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessage;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessageType;

@MessageDriven(mappedName = "java:global/jms/ObserverQueue", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class ChatManagementBean implements MessageListener {
	@Inject
	StatisticManagementLocal statisticManagement;
	
	@Inject
	private JMSContext jmsContext;

	@Resource(lookup = "java:global/jms/ObserverTopic")
	private Topic chatTopic;

	private ArrayList<String> swearwordsList;

	/** Nachricht wird an Clients weitergeleitet
	 * @param massage
	 */
	private void notifyViaObserver(ChatMessage massage) {

		System.out.println("notify");

		ObjectMessage createObjectMessage = jmsContext.createObjectMessage(massage);
		jmsContext.createProducer().send(chatTopic, createObjectMessage);

	}


	@Override
	public void onMessage(Message message) {
		System.out.println("onMessageServer");

		try {
			//Nachrich wird Empfangen, gefiltered und an CLients weitergesendet
			TextMessage textMessage = (TextMessage) message;

			String filteredText = filterSwearwords(textMessage.getText());
			User sender = new User();

			sender.setUsername(textMessage.getStringProperty("Name"));
			UserStatistic userStatistic= new UserStatistic();
			userStatistic.setMessages(1);
			statisticManagement.updateUserStatistic(sender, userStatistic);

			ChatMessage chatMessage = new ChatMessage(ChatMessageType.TEXT, sender.getUsername(), filteredText,
					new Date());

			notifyViaObserver(chatMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Filtert Schimpfwoerter
	 * @param text der gefillter werdden soll
	 * @return
	 */
	private String filterSwearwords(String text) {
		swearwordsList = new ArrayList<>();
		swearwordsList.add("Arsch");
		swearwordsList.add("Penner");
		swearwordsList.add("Hurensohn");

		for (String string : swearwordsList) {
			text = text.replaceAll(string, "****");
		}
		return text;

	}

}
