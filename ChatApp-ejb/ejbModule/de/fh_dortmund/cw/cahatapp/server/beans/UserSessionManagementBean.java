package de.fh_dortmund.cw.cahatapp.server.beans;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Singleton;
import javax.ejb.Stateful;

import de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserManagementLocal;
import de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserSessionManagementLocal;
import de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserSessionManagementRemote;
import de.fh_dortmund.cw.chatapp.server.entities.User;
import de.fh_dortmund.cw.chatapp.server.exception.LoginException;
import de.fh_dortmund.cw.chatapp.server.exception.LogoutException;

@Stateful
public class UserSessionManagementBean implements UserSessionManagementLocal, UserSessionManagementRemote {

	User user;
	boolean online;

	@EJB
	private UserManagementLocal userManagement; // Acess the userManagement Bean

	@PostConstruct
	public void init() {
		user = new User();
	}

	@Override
	public void logout() {

		// Remove this user from OnlineList
		if (online)
			userManagement.logout(user);

		disconnect();

	}

	@Remove
	@Override
	public void disconnect() {

	}

	@Override
	public String getUsername() {

		return user.getUsername();
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		userManagement.changePasswort(user, newPassword);

	}

	@Override
	public void delete(String password) throws LogoutException {
		if (password.equals(user.getPassword())) {
			userManagement.delete(user);
			logout();
			
		}

	}

	@Override
	public void login(String name, String password) throws LoginException {
		userManagement.login(name, password); // Add to OnlineUser

		online = true;
		user.setUsername(name);
		user.setPassword(password);

	}

}
