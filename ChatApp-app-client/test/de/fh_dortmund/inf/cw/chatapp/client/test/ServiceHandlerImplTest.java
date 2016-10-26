package de.fh_dortmund.inf.cw.chatapp.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.fh_dortmund.cw.chat.client.ServiceHandlerImpl;
import de.fh_dortmund.inf.cw.chat.server.exception.LoginException;
import de.fh_dortmund.inf.cw.chat.server.exception.PasswordException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ServiceHandlerImplTest {

	private static ServiceHandlerImpl impl;
	private static String name;
	private static String pass;

	@BeforeClass
	public static void setUp() throws Exception {
		
		impl = ServiceHandlerImpl.getInstance();
		name=UUID.randomUUID().toString();
		pass=UUID.randomUUID().toString();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void t001_testRegister() throws Exception {
		
		int i= impl.getNumberOfRegisteredUsers();
		
		impl.register(name, pass);
		assertEquals(i+1, impl.getNumberOfRegisteredUsers());
		
	}

//	@Test(expected=RegisterException.class)
//	public void t002_registerFail() throws Exception {
//		
//		impl.register(null, null);
//	}
	@Test
	public void t003_testLoginAndOnlineUsers() throws Exception {
		int numberOfOnlineUsers = impl.getNumberOfOnlineUsers();
		impl.login(name, pass);
		assertEquals(numberOfOnlineUsers+1, impl.getNumberOfOnlineUsers());
		
	}
	@Test(expected=LoginException.class)
	public void t0031_testLoginFail() throws Exception{
		impl.login("a", "t");

	}
	
	
	
	@Test
	public void t004_testChangePassword() throws Exception {
		String newpass="test";
		impl.changePassword(pass, newpass);
		impl.changePassword(newpass, pass);
		
	}
	
	@Test(expected=PasswordException.class)
	public void t005_changePasswordFail() throws Exception {
		impl.changePassword("Testofalsch", "test");
	}

	@Test
	public void t006_testGetOnlineUsers ()throws Exception {
		
		boolean t= false;
		for (String s : impl.getOnlineUsers()) {
			if(s.equals(name))
			{
				t=true;
				break;
			}
			
		}
		assertTrue(t);
		
	}

	@Test
	public void t007_testGetNumberOfRegisteredUsers() throws Exception {
		
		int numberOfRegisteredUsers = impl.getNumberOfRegisteredUsers();
		impl.register(UUID.randomUUID().toString(), UUID.randomUUID().toString());
		assertEquals(numberOfRegisteredUsers+1, impl.getNumberOfRegisteredUsers());
	}

	@Test
	public void t008_testGetUserName() {
		
		assertEquals(name, impl.getUserName());
		
	}

	@Test
	public void t_009testLogout() throws Exception {
		int i=impl.getNumberOfOnlineUsers();
		
		impl.logout();
		
		assertEquals(i-1, impl.getNumberOfOnlineUsers());
		
	}
	@Test
	public void testDisconnect() {
		impl.disconnect();
	}

	@Test
	public void testDelete() throws Exception {
		impl.login(name, pass);
		impl.delete(pass);
		
	}
		
		
	

}
