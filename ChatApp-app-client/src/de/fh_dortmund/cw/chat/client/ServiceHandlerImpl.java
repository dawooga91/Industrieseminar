package de.fh_dortmund.cw.chat.client;



import javax.naming.Context;

import de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserManagement;
import de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserManagementRemote;
import de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserSessionManagement;
import de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserSessionManagementRemote;
import de.fh_dortmund.inf.cw.chat.client.shared.ServiceHandler;

public class ServiceHandlerImpl extends ServiceHandler implements UserSessionManagement, UserManagement {
	
	private Context ctx;
	private UserSessionManagementRemote sessionManagement;
	private UserManagementRemote userManagement;
	
	private static ServiceHandlerImpl instance;
	
	private ServiceHandlerImpl() {
		
//		try {
//			ctx= new InitialContext();
//			sessionManagement= (UserSessionManagementRemote) ctx.lookup("java:global/ChatApp-ear/ChatApp-ejb/UserSessionManagementBean!de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserSessionManagementRemote");
//			userManagement=(UserManagementRemote) ctx.lookup("java:global/ChatApp-ear/ChatApp-ejb/UserManagementBean!de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserManagementRemote");
//			System.out.println("test");
//		} catch (NamingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	public static ServiceHandlerImpl getInstance() {
		if(instance == null) {
			instance = new ServiceHandlerImpl();
		}
		
		return instance;
	}
	
	
	@Override
	public String[] getOnlineUsers() {
		
//		return userManagement.getOnlineUsers();
		return new String[]{};
	}

	@Override
	public int getNumberOfRegisteredUsers() {
		
//		return userManagement.getNumberOfRegisteredUsers();
	return 0;
	}

	@Override
	public int getNumbersOfOnlineUseres() {
//		return userManagement.getNumbersOfOnlineUseres();
	return 0;
	}

	@Override
	public void login(String name, String passwort) {
//		sessionManagement.login(name, passwort);
		
	}

	@Override
	public void logout() {
//		sessionManagement.logout();
		
	}

	@Override
	public void disconnect() {
//		sessionManagement.disconnect();
		
	}

	@Override
	public String getUsername() {
//		return sessionManagement.getUsername();
	return "N/A";
	}

	@Override
	public void register(String name, String password) {
//		userManagement.register(name, password);
		
	}

	
}
