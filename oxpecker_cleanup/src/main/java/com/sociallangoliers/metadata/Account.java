package com.sociallangoliers.metadata;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Samir Faci
 * Date: 8/8/12
 * Time: 8:46 PM
 */
public interface Account {
	public String getUser();
	public void   setUser(String value);
	public String getToken();
	public void setToken(String value);
	public String getSecret();
	public void setSecret(String value);
	public void setDelta(long time);
	public Date getDelta();

}
