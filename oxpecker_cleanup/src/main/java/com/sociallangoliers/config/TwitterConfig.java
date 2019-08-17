package com.sociallangoliers.config;

import java.util.*;

import com.sociallangoliers.support.SocialAction;
import lombok.Data;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import twitter4j.auth.RequestToken;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 1/15/13
 * Time: 11:26 PM
 */
@Component
@ConfigurationProperties("twitter")
@Data
public class TwitterConfig {

    private Map<SocialAction, Boolean> twitterActions = new HashMap<SocialAction, Boolean>();

    private String consumer;
    private String secret;
    private RequestToken token;
    private int incrementCount = 0;
    boolean directMessages;
	boolean lists;
	boolean tweets;
	boolean favorites;
	boolean retweets;
	boolean friendships;

    @Autowired
	public TwitterConfig(
		@Value("${twitter.consumer:default}") final String consumer,
						 @Value("${twitter.secret:secret}") final String secret,
						 @Value("${twitter.incrementCount:199}") final int incrementCount) {

		this.token = new RequestToken(consumer, secret);
		this.incrementCount = incrementCount;
	}

	public TwitterConfig(Map values) {
        loadTwitterConfig(values);

    }
        /**
	 * Load data specific to twitter into memory.
	 */
	private void loadTwitterConfig(Map prop) {

        String consumer = null;
        String secret = null;

        if (prop.containsKey("twitter.consumer"))
            consumer = (String) prop.get("twitter.consumer");

        if (prop.containsKey("twitter.secret"))
            secret = (String) prop.get("twitter.secret");

        if (secret != null && consumer != null) {
            this.token = new RequestToken(consumer, secret);

        }

        if (prop.get("twitter.incrementCount") != null)
            incrementCount = NumberUtils.toInt((String) prop.get("twitter.incrementCount"), 199);

        twitterActions.put(SocialAction.directmessages, BooleanUtils.toBoolean((String) prop.get("twitter.DirectMessages")));
        twitterActions.put(SocialAction.lists, BooleanUtils.toBoolean((String) prop.get("twitter.lists")));
        twitterActions.put(SocialAction.posts, BooleanUtils.toBoolean((String) prop.get("twitter.tweets")));
        twitterActions.put(SocialAction.favorites, BooleanUtils.toBoolean((String) prop.get("twitter.favorites")));
        twitterActions.put(SocialAction.reposts, BooleanUtils.toBoolean((String) prop.get("twitter.retweets")));
        twitterActions.put(SocialAction.friendships, BooleanUtils.toBoolean((String) prop.get("twitter.friendships")));

    }

	public boolean isCleanDirectMessages() {
		return directMessages;
	}


	public boolean isCleanFavorites() {
		return favorites;
	}

	public boolean isCleanLists() {
		return lists;
	}

	public boolean isCleanFriendships() {
		return friendships;
	}

	public boolean isCleanTweets() {
		return tweets;
	}

	public boolean isCleanReTweets() {
		return retweets;
	}

	public RequestToken getToken() {
		if(token == null) {
			token = new RequestToken(consumer, secret);
		}

		return token;
	}


}
