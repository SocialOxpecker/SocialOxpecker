package com.sociallangoliers.services;

import com.google.common.collect.Maps;
import com.sociallangoliers.support.SocialAction;
import com.sociallangoliers.support.SocialCode;
import org.scribe.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class SocialAdapter {
    Map<SocialCode, PruneSocial> socialServices = Maps.newHashMap();


    @Autowired
    public SocialAdapter(@Qualifier("twitter") final PruneSocial twitter,
                         @Qualifier("facebook") final PruneSocial facebook,
                         @Qualifier("reddit") final PruneSocial reddit
                         ) {
        socialServices.put(SocialCode.twitter, twitter);
        socialServices.put(SocialCode.facebook, facebook);
        socialServices.put(SocialCode.reddit, reddit);
    }

    public PruneSocial getSocialService(SocialCode socialCode) {
        if(socialServices.containsKey(socialCode)) {
            return socialServices.get(socialCode);
        }
        throw new RuntimeException("Invalid Social Service requested");
    }


    public boolean cleanup(SocialCode socialCode, SocialAction action, Object appToken, Object userAuthToken, LocalDateTime localDateTime) {
        return getSocialService(socialCode).cleanup(action, appToken, userAuthToken, localDateTime);
    }

    public Token getAccessToken(SocialCode socialCode) {
        return getSocialService(socialCode).getAccessToken();
    }


    public String validate(SocialCode socialCode, String clientId, String secret, String accessToken, String secretToken) {
        //foobar
        return "www.google.com";
    }
}
