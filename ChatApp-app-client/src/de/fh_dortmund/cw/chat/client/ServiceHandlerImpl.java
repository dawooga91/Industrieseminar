package de.fh_dortmund.cw.chat.client;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.fh_dortmund.cw.chatapp.server.beans.interfaces.PasswordHandlerRemote;
import de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserManagementRemote;
import de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserSessionManagementRemote;
import de.fh_dortmund.inf.cw.chat.client.shared.ServiceHandler;
import de.fh_dortmund.inf.cw.chat.client.shared.UserSessionHandler;

public class ServiceHandlerImpl extends ServiceHandler implements UserSessionHandler {

	private Context ctx;
	private UserSessionManagementRemote sessionManagement;
	private UserManagementRemote userManagement;

	private static ServiceHandlerImpl instance;
	private PasswordHandlerRemote passwordHandler;

	private ServiceHandlerImpl() {

		try {
			ctx = new InitialContext();
			sessionManagement = (UserSessionManagementRemote) ctx.lookup(
					"java:global/ChatApp-ear/ChatApp-ejb/UserSessionManagementBean!de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserSessionManagementRemote");
			userManagement = (UserManagementRemote) ctx.lookup(
					"java:global/ChatApp-ear/ChatApp-ejb/UserManagementBean!de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserManagementRemote");
			passwordHandler = (PasswordHandlerRemote) ctx.lookup("java:global/ChatApp-ear/ChatApp-ejb/PasswordHandlerBean!de.fh_dortmund.cw.chatapp.server.beans.interfaces.PasswordHandlerRemote");
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

	@Override
	public void changePassword(String oldPassword, String newPassword) throws Exception {
		sessionManagement.changePassword( passwordHandler.getHashPassword(oldPassword),  passwordHandler.getHashPassword(newPassword));
		
	}

	@Override
	public void delete(String password) throws Exception {
	sessionManagement.delete(passwordHandler.getHashPassword(password));
	}

	@Override
	public void disconnect() {
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
	}

	@Override
	public void logout() throws Exception {
		sessionManagement.logout();
	}

	@Override
	public void register(String name, String password) throws Exception {
		userManagement.register(name, password);
	}

}
