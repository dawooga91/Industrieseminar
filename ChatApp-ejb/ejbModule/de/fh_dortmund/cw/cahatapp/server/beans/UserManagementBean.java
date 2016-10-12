package de.fh_dortmund.cw.cahatapp.server.beans;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserManagementLocal;
import de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserManagementRemote;
import de.fh_dortmund.cw.chatapp.server.entities.User;

@Singleton
@Startup
public class UserManagementBean implements UserManagementLocal, UserManagementRemote {
	private LinkedHashMap<Long, User> users;
	private long lastUserNumber = 0;

	private ArrayList<User> onlineUsers;

	@PostConstruct
	private void init() {
		users = new LinkedHashMap<Long, User>();
	}

	@Lock(LockType.READ)
	@Override
	public String[] getOnlineUsers() {
		String[] ret = new String[onlineUsers.size()];
		int i = 0;
		for (User user : onlineUsers) {
			ret[i] = user.getUsername();
			i++;
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
	public void register(String name, String password) {

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
	public void login(String name, String password) {
		for (Entry<Long, User> entry : users.entrySet()) {

			if (entry.getValue().getUsername().equals(name)) {
				if (entry.getValue().getPassword().equals(password)) {

					onlineUsers.add(entry.getValue());
					return;
				}
			}
		}
		throw new IllegalArgumentException("Password oder Nutzername sind falsch");

	}
	@Lock(LockType.WRITE)
	@Override
	public void logout(User user) {
		for (User userO : onlineUsers) {
			if (user.getUsername().equals(userO.getUsername())) {
				onlineUsers.remove(userO);
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

}
