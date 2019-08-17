package com.sociallangoliers.services;

import com.sociallangoliers.support.SocialAction;
import org.scribe.model.Token;
import scala.Tuple2;
import twitter4j.auth.RequestToken;

import java.time.LocalDateTime;

public interface PruneSocial {

    boolean execute();

    /**
     * true if succeeded.
     * @return
     */
    boolean cleanup(SocialAction action, Object appToken, Object userAuthToken, LocalDateTime localDateTime);

    Token getAccessToken();

    String authenticate(RequestToken appToken);

    Tuple2<String, String> validate(RequestToken appToken, String pin) ;


}
