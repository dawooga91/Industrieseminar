package de.fh_dortmund.cw.chatapp.server.beans.interfaces;

import javax.ejb.Local;

import de.fh_dortmund.cw.chatapp.server.entities.User;

@Local
public interface UserManagementLocal extends UserManagement {

	
	public void delete(User user);


	public void login(String username,String password);

	public void logout(User user);

	void changePasswort(User user, String password);
}
