package com.sociallangoliers.actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.sociallangoliers.services.PruneSocial;
import com.sociallangoliers.services.SocialAdapter;
import com.sociallangoliers.support.SocialAction;
import lombok.Builder;
import lombok.Getter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TwitterWorker extends AbstractLoggingActor {
    //protocol
//        @Data
    public static class TwitterMessageRequest {
        @Getter
        private final SocialAction action;
        @Getter
        private final LocalDateTime miniMumDate; // older then value
        @Getter
        private final RequestToken appToken;
        @Getter
        private final AccessToken accessToken;
        @Getter
        private final PruneSocial socialPrune;


        @Builder
        public TwitterMessageRequest(String consumer, String secret, SocialAction action, long lookbackWindow,
                                     PruneSocial socialPrune,
                                     RequestToken requestToken
        ) {
            this.appToken = requestToken;
            this.accessToken = new AccessToken(consumer, secret);
            this.action = action;
            LocalDateTime today = LocalDateTime.now();
            miniMumDate = today.minus(lookbackWindow, ChronoUnit.DAYS);
            this.socialPrune = socialPrune;
        }
    }

    @Override
    public Receive createReceive() {
        log().debug("Received a message on akka twitter");
        Receive build = ReceiveBuilder.create()
            .match(TwitterMessageRequest.class, this::onMessage)
            .build();

        return build;
    }

    private <P> void onMessage(TwitterMessageRequest msg) {
        if (msg.socialPrune != null) {
            msg.socialPrune.cleanup(msg.action, msg.appToken, msg.accessToken, msg.miniMumDate);
        }
    }

    public static Props props() {
        return Props.create(TwitterWorker.class);
    }

}
