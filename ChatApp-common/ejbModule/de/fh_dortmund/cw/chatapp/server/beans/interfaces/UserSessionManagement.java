package de.fh_dortmund.cw.chatapp.server.beans.interfaces;

public interface UserSessionManagement {
	
	public void login(String name, String passwort);
	
	public void logout();
	
	public void disconnect();
	
	public String getUsername();
	

}
