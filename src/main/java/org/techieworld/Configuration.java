package org.techieworld;

import org.apache.log4j.Logger;
import org.techieworld.metadata.Account;
import twitter4j.http.AccessToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Samir Faci
 * Date: 8/8/12
 * Time: 8:44 PM
 */
public class Configuration {
	Logger logger = Logger.getLogger(Configuration.class);
	private static Configuration instance = new Configuration();
	List<Account> accounts = new ArrayList<Account>();
	AccessToken token;

	public Configuration getInstance() {
		return instance;
	}


	private Configuration()
	{

		Properties properties = new Properties();

        Properties p = new Properties();
        try
        {
            InputStream in = Main.class.getResourceAsStream("/instance.properties");
            if(in == null)
            {
                logger.info("Could not read jar resource /instance.properties using class defaults");
                return;
            }
            p.load(in);
        }
        catch(IOException f)
        {
	        logger.error("Could not load properties, it is null ... using class defautls", f);
            return;
        }//end outer try

        //Load Data
		/**
        if(p.get("google.username") != null)
            username = (String) p.get("google.username");

        if(p.get("google.password") != null)
            pass = (String) p.get("google.password");

        if(p.get("google.phone") != null)
            phone = (String) p.get("google.phone");

       if(p.get("google.message") != null)
            message = (String) p.get("google.message");

		*/


	}
}
