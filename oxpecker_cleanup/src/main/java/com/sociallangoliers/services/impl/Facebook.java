package com.sociallangoliers.services.impl;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.types.Post;
import com.restfb.types.User;
import com.sociallangoliers.config.Configuration;
import com.sociallangoliers.config.FacebookConfig;
import com.sociallangoliers.services.PruneSocial;
import lombok.extern.log4j.Log4j2;
import org.apache.log4j.BasicConfigurator;

import java.util.*;

import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * User: Samir Faci
 * Date: 1/7/13
 * Time: 8:36 PM
 */


@Log4j2
@Service
@Qualifier("facebook")
public class Facebook implements PruneSocial {
    private Configuration config = Configuration.getInstance();
    private static final String NETWORK_NAME = "Facebook";
    private static final Token EMPTY_TOKEN = null;

    static {
        BasicConfigurator.configure();
    }


    @Override
    public boolean execute() {
        //TODO: figure out why writing to FB isn't working or figure out the proper syntax to use.
        FacebookConfig fbconfig = config.getFbConfig();
        Token token = fbconfig.getToken();
        if (token == null) {
            token = getAccessToken();
            fbconfig.setToken(token);
        }

        FacebookClient client = new DefaultFacebookClient(Version.VERSION_4_0);
        // token.getToken()

        User user = client.fetchObject("me", User.class);
        Connection<User> myFriends = client.fetchConnection("me/friends", User.class);
        Connection<Post> myFeed = client.fetchConnection("me/feed", Post.class);

        System.out.println("Count of my friends: " + myFriends.getData().size());
        System.out.println("First item in my feed: " + myFeed.getData().get(0));

        // Connections support paging and are iterable

        for (List<Post> myFeedConnectionPage : myFeed)
            for (Post post : myFeedConnectionPage)
                System.out.println("Post: " + post);


        //GraphReaderExample.FetchObjectsResults fetchObjectsResults =
        //client.fetchObjects(Arrays.asList("me", "status"), GraphReaderExample.FetchObjectsResults.class);
        System.out.println(user.getName());
        System.out.println(user.getBirthday());
        //client.publish(post.getId()+"/comments", String.class, Parameter.with("message", "Your comment here"));
        //FacebookType publishMessageResponse = client.publish("me/feed", FacebookType.class, Parameter.with("message", "RestFB test"));
//        FacebookType publishMessageResponse = client.publish("me/feed",
//                FacebookType.class, new Parameter("message", "RestFB test"),new Parameter("link", "http://www.google.com"));
        Parameter p = new Parameter("link", "http://www.google.com");
        //client.publish("me/feed", FacebookType<?>.class, p );
        //<T> T publish(String connection, Class<T> objectType, Parameter... parameters);
        //
        Map<String, String> coordinates = new HashMap<String, String>();
        coordinates.put("latitude", "37.06");
        coordinates.put("longitude", "-95.67");

//        FacebookType publishCheckinResponse = client.publish("me/checkins",
//            FacebookType.class, new Parameter("message", "I'm here!"),
//                new Parameter("coordinates", coordinates), new Parameter("place", 1234)));

        return true;
    }

    @Override
    public Token getAccessToken() {
        FacebookConfig fbconfig = config.getFbConfig();
        String apiKey = fbconfig.getClientId();
        String apiSecret = fbconfig.getSecret();
        OAuthService service = new ServiceBuilder()
                .provider(FacebookApi.class)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(fbconfig.getCallbackURL())
                .build();
        Scanner in = new Scanner(System.in);

        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();

        // Obtain the Authorization URL
        System.out.println("Fetching the Authorization URL...");
        String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
        System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize Scribe here:");
        System.out.println(authorizationUrl);
        System.out.println("And paste the authorization code here");
        System.out.print(">>");
        Verifier verifier = new Verifier(in.nextLine());
        System.out.println();

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken + " )");
        System.out.println();
        return accessToken;

    }
}