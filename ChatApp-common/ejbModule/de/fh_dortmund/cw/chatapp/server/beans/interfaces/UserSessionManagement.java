package de.fh_dortmund.cw.chatapp.server.beans.interfaces;

import de.fh_dortmund.cw.chatapp.server.exception.LoginException;
import de.fh_dortmund.cw.chatapp.server.exception.LogoutException;

public interface UserSessionManagement {
	
	public void login(String name, String passwort) throws LoginException;
	
	public void logout();
	
	public void disconnect();
	
	public String getUsername();
	
	public void changePassword(String oldPassword, String newPassword);
	
	public void delete(String password) throws LogoutException;
	

}
