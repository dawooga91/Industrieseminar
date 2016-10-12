package de.fh_dortmund.cw.chatapp.server.beans.interfaces;

import java.util.List;

import de.fh_dortmund.cw.chatapp.server.entities.User;

public interface UserManagement {

	public List<String> getOnlineUsers();

	public int getNumberOfRegisteredUsers();

	public int getNumbersOfOnlineUseres();
	
	public void register(String name, String password) throws IllegalArgumentException;


}
