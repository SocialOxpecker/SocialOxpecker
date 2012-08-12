package com.sociallangoliers.config;

import org.apache.commons.lang.StringUtils;
import org.scribe.model.Token;

import java.util.Map;

public class FacebookConfig {
    private Token token;
    private String callbackURL;

    public FacebookConfig(Map values) {
        clientId = (String) values.get("facebook.clientId");
        secret = (String) values.get("facebook.secret");
        callbackURL = (String) values.get("facebook.callback");
        if (values.containsKey("facebook.userToken")) {
            String t = (String) values.get("facebook.userToken");
            if (StringUtils.isNotEmpty(t)) {
                Token localToken = new Token(t, "");
                setToken(localToken);
            }
        }

    }


    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }


    public String getCallbackURL() {
        return callbackURL;
    }


    private String clientId;
    private String secret;

    public String getClientId() {
        return clientId;
    }

    public String getSecret() {
        return secret;
    }


}
