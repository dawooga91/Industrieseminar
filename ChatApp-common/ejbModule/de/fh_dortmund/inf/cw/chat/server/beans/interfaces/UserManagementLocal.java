package de.fh_dortmund.inf.cw.chat.server.beans.interfaces;

import javax.ejb.Local;

import de.fh_dortmund.inf.cw.chat.server.entities.User;
import de.fh_dortmund.inf.cw.chat.server.exception.LoginException;

@Local
public interface UserManagementLocal extends UserManagement {

	
	public void delete(User user);


	public void login(String username,String password) throws LoginException;

	public void logout(User user);

	public void changePasswort(User user, String password);
}
