package de.fh_dortmund.cw.chat.client;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserManagementRemote;
import de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserSessionManagementRemote;
import de.fh_dortmund.inf.cw.chat.client.shared.ServiceHandler;
import de.fh_dortmund.inf.cw.chat.client.shared.UserSessionHandler;

public class ServiceHandlerImpl extends ServiceHandler implements UserSessionHandler {

	private Context ctx;
	private UserSessionManagementRemote sessionManagement;
	private UserManagementRemote userManagement;

	private static ServiceHandlerImpl instance;

	private ServiceHandlerImpl() {

		try {
			ctx = new InitialContext();
			sessionManagement = (UserSessionManagementRemote) ctx.lookup(
					"java:global/ChatApp-ear/ChatApp-ejb/UserSessionManagementBean!de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserSessionManagementRemote");
			userManagement = (UserManagementRemote) ctx.lookup(
					"java:global/ChatApp-ear/ChatApp-ejb/UserManagementBean!de.fh_dortmund.cw.chatapp.server.beans.interfaces.UserManagementRemote");
			System.out.println("test");
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
	public void changePassword(String arg0, String arg1) throws Exception {

	}

	@Override
	public void delete(String arg0) throws Exception {

	}

	@Override
	public void disconnect() {

	}

	@Override
	public int getNumberOfOnlineUsers() {
		return 0;
	}

	@Override
	public int getNumberOfRegisteredUsers() {
		return 0;
	}

	@Override
	public List<String> getOnlineUsers() {
		return new ArrayList(0);
	}

	@Override
	public String getUserName() {
		return "N/A";
	}

	@Override
	public void login(String arg0, String arg1) throws Exception {

	}

	@Override
	public void logout() throws Exception {

	}

	@Override
	public void register(String arg0, String arg1) throws Exception {

	}

}
