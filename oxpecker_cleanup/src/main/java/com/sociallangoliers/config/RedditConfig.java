package com.sociallangoliers.config;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 1/15/13
 * Time: 11:26 PM
 */
public class RedditConfig {
    private String secret;
    private String consumer;

    public String getSecret() {
        return secret;
    }

    public String getConsumer() {
        return consumer;
    }

    public RedditConfig(Map values) {
        consumer= (String) values.get("reddit.consumer");
        secret = (String) values.get("reddit.secret");

    }
}
