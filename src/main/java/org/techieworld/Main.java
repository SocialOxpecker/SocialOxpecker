package org.techieworld;


import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: Samir Faci
 * Date: 8/8/12
 * Time: 8:22 PM
 */
public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);
	private static final long twoWeeks = 14 * 24*60*60 * 1000;
	static Twitter twitter = new TwitterFactory().getInstance();

	static {
		twitter.setOAuthConsumer("x17j0JX3OpeH32bjYXB3w", "QfOoFSIZHCy2k7156frRMk8ZITwxImQE0qDXslL5zI");
        BasicConfigurator.configure();
	}

	public static AccessToken requestToken()
	{
		RequestToken requestToken = null;
		try {
			requestToken = twitter.getOAuthRequestToken();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		AccessToken accessToken = null;
		Scanner scanner = new Scanner(System.in);
		while (null == accessToken) {
			System.out.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());
			System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
			String pin = scanner.nextLine();
			try{
				if(pin.length() > 0){
					accessToken = twitter.getOAuthAccessToken(requestToken, pin);
				}else{
					accessToken = twitter.getOAuthAccessToken();
				}
			} catch (TwitterException te) {
				if(401 == te.getStatusCode()){
					System.out.println("Unable to get the access token.");
				}else{
					te.printStackTrace();
				}
			}
		}
		//persist to the accessToken for future reference.
		try {
			storeAccessToken(twitter.verifyCredentials().getId(), accessToken);
		} catch (TwitterException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return accessToken;

	}

	private static void storeAccessToken(long id, AccessToken accessToken) {

		logger.info("id: " + id);
		logger.info("token:" + accessToken.getToken());
		logger.info("secret" + ":" + accessToken.getTokenSecret());
	}

	public static void main(String[] args) {
        logger.info("WARNING!!!!!!  Once authorized, this will delete EVERY tweet you ever posted, that is older then 2 weeks.\n" +
        "Do not continue unless you are absolutely certain you want to do this.");
        Scanner scanner = new Scanner(System.in);
        logger.info("Do you wish to continue? (y/n)?");
        String ans = scanner.nextLine();

        if(ans.toLowerCase().charAt(0) != 'y' ) {
            logger.info("Exciting application....");
            System.exit(0);
        }

		AccessToken token = requestToken();
		twitter.setOAuthAccessToken(token);

		Date earlier = new Date(System.currentTimeMillis() - twoWeeks);
		logger.info(earlier.toString());

		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();


		try {
			boolean found = true;
			int start = 1;
			int increment = 199;
			while(found)
			{
				found = false;
				List<Status> status =  twitter.getUserTimeline(new Paging(start,start+increment));
				//start = start+increment;
				for(Status s : status) {
					logger.info(s.getCreatedAt() + ":" + s.getText());
					if(earlier.getTime() > s.getCreatedAt().getTime())
					{
						logger.info("deleting item: " + s.getId());
						found = true;
						twitter.destroyStatus(s.getId());
					}
				}
				logger.debug("size of status is: {} ", status.size());
			}
		} catch (TwitterException e) {
			logger.error("Twitter exception occurred", e);
		}
	}

}
