package com.sociallangoliers.config;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Samir Faci
 * Date: 8/8/12
 * Time: 8:44 PM
 */

/**
 * Global Generic Configuration.
 */
public class Configuration {

	Logger logger = LoggerFactory.getLogger(Configuration.class);
	private static final Configuration instance = new Configuration();
	private long delta = 0;

    Map<String, TwitterConfig> twitterConfig = new HashMap<String, TwitterConfig>();
    RedditConfig redditConfig;
    FacebookConfig fbConfig;
    private List<String> services = new ArrayList<String>();

    public TwitterConfig getTwitterConfig(String account) {
        return twitterConfig.get(account);
    }

    public RedditConfig getRedditConfig() {
        return redditConfig;
    }

    public FacebookConfig getFbConfig() {
        return fbConfig;
    }

	private Configuration() {
		setDelta(14);
		Properties prop = new Properties();
		try {
			File file = new File("instance.properties"); // look in current directory for file.
			InputStream stream = new FileInputStream(file);
			prop.load(stream);
		} catch (Exception f) {

			logger.warn("Property file was not found in search path, falling back on jar properties");
			try {
				InputStream in = Configuration.class.getResourceAsStream("/instance.properties");
				if (in == null) {
					logger.info("Could not read jar resource /instance.properties using class defaults");
					return;
				}
				prop.load(in);
			} catch (IOException e) {
				logger.error("Could not load properties, it is null ... using class defautls", e);
                return;
			}
		} //end outer try

		//Load Data
		loadData(prop);

	}

	/**
	 * Load data from property files into memory.
	 * @param prop
	 */
	private void loadData(Properties prop) {
		//Global configs.
		int days = NumberUtils.toInt((String) prop.get("global_app.delta"), -1);
		if(days != -1) {
			setDelta(days);
		}

		//Twitter Configs

		//loadTwitterConfig(prop);
        //loadReddit(prop);
        Map<String, String> map = new HashMap<String, String>((Map) prop);
        twitterConfig.put("twitter", new TwitterConfig(map));
        redditConfig = new RedditConfig(map);
        fbConfig = new FacebookConfig(map);

        logger.info("Size of map is {}", map.size());


	}

 	public static Configuration getInstance() {
		return instance;
	}

	public long getDelta() {
		return delta;
	}

	public void setDelta(int days) {
		delta = days * 24*60*60 * 1000;
	}



}
