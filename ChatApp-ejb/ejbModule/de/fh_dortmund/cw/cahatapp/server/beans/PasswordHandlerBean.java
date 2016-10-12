package de.fh_dortmund.cw.cahatapp.server.beans;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Resource;
import javax.ejb.Stateless;

import de.fh_dortmund.cw.chatapp.server.beans.interfaces.PasswordHandlerLocal;
import de.fh_dortmund.cw.chatapp.server.beans.interfaces.PasswordHandlerRemote;

@Stateless
public class PasswordHandlerBean implements PasswordHandlerLocal, PasswordHandlerRemote {

	@Resource(name = "hashMethode")
	String hashmethode;

	@Override
	public String getHashPassword(String password) {
		String hash;
		try {
			MessageDigest encoder = MessageDigest.getInstance(hashmethode);
			hash = String.format("%040x", new BigInteger(1, encoder.digest(password.getBytes())));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Passwordcould compile");
		}
		return hash;
	}

}
