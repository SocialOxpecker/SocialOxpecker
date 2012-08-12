package com.sociallangoliers.metadata.impl;

import com.sociallangoliers.metadata.Account;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Samir Faci
 * Date: 8/8/12
 * Time: 8:49 PM
 */
@Service
public class TwitterAccount implements Account {
	String userName;
	String token;
	String secret;
	long delta;

	@Override
	public String getUser() {
		return userName;
	}

	@Override
	public void setUser(String userName) {
		this.userName = userName;
	}

	@Override
	public String getToken() {
		return token;
	}

	@Override
	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String getSecret() {
		return secret;
	}

	@Override
	public void setSecret(String secret) {
		this.secret = secret;
	}

	public Date getDelta() {
		return new Date(delta);
	}

	@Override
	public void setDelta(long delta) {
		this.delta = delta;
	}

}
