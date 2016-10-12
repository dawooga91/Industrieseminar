package de.fh_dortmund.cw.chatapp.server.beans.interfaces;

public interface UserManagement {

	public String[] getOnlineUsers();

	public int getNumberOfRegisteredUsers();

	public int getNumbersOfOnlineUseres();
	
	public void register(String name, String password);


}
