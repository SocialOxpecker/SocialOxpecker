package com.sociallangoliers.services.impl;


import com.sociallangoliers.config.Configuration;
import com.sociallangoliers.config.TwitterConfig;
import com.sociallangoliers.services.PruneSocial;
import lombok.extern.log4j.Log4j2;
import org.scribe.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import twitter4j.DirectMessage;
import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UserList;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

@Service
@Qualifier("twitter")
@Log4j2
public class Twitter implements PruneSocial {
    Token accessToken= null;
    private final twitter4j.Twitter twitter;
    private final Date earlier;
    Configuration config = null;
    TwitterConfig twitterConfig;
    RequestToken token = null;

    @Autowired
    public Twitter(TwitterConfig twitterConfig,
                   @Value("${global_app.delta:17}") int delta) {
        twitter = new TwitterFactory().getInstance();
        token = twitterConfig.getToken();
        twitter.setOAuthConsumer(twitterConfig.getToken().getToken(), twitterConfig.getToken().getTokenSecret());
        earlier = new Date(System.currentTimeMillis() - delta);
        this.twitterConfig = twitterConfig;
    }


    /**
     * In theory this could persis the token.  For now, due to the excessively intrusive nature
     * of this application.  I'd rather require the user to re-authorize the application.  The
     * token and secret are printed to console for debugging purposes mainly.
     *
     * @param id
     * @param accessToken
     */
    private static void storeAccessToken(long id, AccessToken accessToken) {

        log.info("id: " + id);
        log.info("token:" + accessToken.getToken());
        log.info("secret" + ":" + accessToken.getTokenSecret());
    }


    private AccessToken requestToken() {
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
            System.out.print("Enter the PIN(if available) or just hit enter.[PIN]:");
            String pin = scanner.nextLine();
            try {
                if (pin.length() > 0) {
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                } else {
                    accessToken = twitter.getOAuthAccessToken();
                }
            } catch (TwitterException te) {
                if (401 == te.getStatusCode()) {
                    System.out.println("Unable to get the access token.");
                } else {
                    te.printStackTrace();
                }
            }
        }
        //persist to the accessToken for future reference.
        try {
            storeAccessToken(twitter.verifyCredentials().getId(), accessToken);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return accessToken;

    }

    public Token getAccessToken() {
        if(accessToken  != null) {
            return accessToken;
        }
        AccessToken access = requestToken();
        this.accessToken = new Token(access.getToken(), access.getTokenSecret());
        return accessToken;

    }


    /**
     * Remove all user lists irrelevant of creation date since that information is
     * not exposed via twitter/ twitter4j.
     * Note: Not time aware.
     */
    private void removeLists() {
        try {
            ResponseList<UserList> lists = twitter.getUserLists(twitter.getScreenName());
            for (UserList list : lists) {
                twitter.destroyUserList(list.getId());
            }

        } catch (TwitterException e) {
            log.error("Twitter exception occurred", e);
        }
    }

    /**
     * Remove all user's friendships (ie. people you are following).
     * Note:  Not time aware.
     */
    private void removeFriendships() {
        try {
            long cursor = -1;
            IDs ids;
            do {
                ids = twitter.getFriendsIDs(cursor);
                for (long id : ids.getIDs()) {
                    twitter.destroyFriendship(id);
                }
            } while ((cursor = ids.getNextCursor()) != 0);
        } catch (TwitterException e) {
            log.error("Twitter exception occurred", e);
        }
    }

    /**
     * removes any retweets that occurred after the time delta given.
     * (ie.  remove any tweets older then 2 weeks)
     */
    private void removeRetweets() {
        try {
            boolean found = true;
            int start = 1;
            int increment = twitterConfig.getIncrementCount();
            while (found) {
                found = false;
                ResponseList<Status> responses = twitter.getRetweetsOfMe(new Paging(start, start + increment));
                for (Status status : responses) {
                    if (earlier.getTime() > status.getCreatedAt().getTime()) {
                        twitter.destroyStatus(status.getId());
                        log.debug("delete dm: " + status.getId());
                        found = true;
                    }
                }
            }
        } catch (TwitterException e) {
            log.error("Twitter exception occurred", e);
        }
    }

    /**
     * Remove any favorited tweets that occured prior to delta given.
     */
    private void removeFavorites() {
        try {
            boolean found = true;
            int start = 1;
            int increment = twitterConfig.getIncrementCount();
            while (found) {
                found = false;
                ResponseList<Status> responses = twitter.getFavorites(new Paging(start, start + increment));
                for (Status status : responses) {
                    if (earlier.getTime() > status.getCreatedAt().getTime()) {
                        twitter.destroyFavorite(status.getId());
                        log.debug("delete dm: " + status.getId());
                        found = true;
                    }
                }
            }
        } catch (TwitterException e) {
            log.error("Twitter exception occurred", e);
        }
    }

    /**
     * remove any direct messages (dm in twitter slang) that were sent and received by the
     * user prior to a certain date)
     */
    public void deleteDirectMessages() {
        try {
            boolean found = true;
            int start = 1;
            int increment = twitterConfig.getIncrementCount();
            //Removing messages sent to user
            while (found) {
                found = false;
                ResponseList<DirectMessage> responses = twitter.getDirectMessages(new Paging(start, start + increment));
                for (DirectMessage dm : responses) {
                    if (earlier.getTime() > dm.getCreatedAt().getTime()) {
                        twitter.destroyDirectMessage(dm.getId());
                        log.debug("delete dm: " + dm.getId());
                        found = true;
                    }
                }
            }
            found = true;
            start = 1;
            increment = twitterConfig.getIncrementCount();
            //Removing messages sent by user.
            while (found) {
                found = false;
                ResponseList<DirectMessage> responses = twitter.getSentDirectMessages(new Paging(start, start + increment));
                for (DirectMessage dm : responses) {
                    if (earlier.getTime() > dm.getCreatedAt().getTime()) {
                        twitter.destroyDirectMessage(dm.getId());
                        log.debug("delete dm: " + dm.getId());
                        found = true;
                    }
                }
            }

        } catch (TwitterException e) {
            log.error("Twitter exception occurred", e);
        }

    }

    /**
     * Delete normal tweets sent by the users that match the delta requirements.
     * (ie. delete tweets older then 2 weeks. Default )
     */
//    public void deleteReTweets() {
//        try {
//            boolean found = true;
//            int start = 1;
//            int increment = twitterConfig.getIncrementCount();
//            while (found) {
//                found = false;
//                List<Status> status = twitter.getUserTimeline(new Paging(start, start + increment));
//                for (Status s : status) {
//                    if (earlier.getTime() > s.getCreatedAt().getTime()) {
//                        found = true;
//                        twitter.destroyStatus(s.getId());
//                        log.debug("removing tweeit with id: {}", s.getId());
//                    }
//                }
//                log.debug("size of status batch is: {} ", status.size());
//            }
//        } catch (TwitterException e) {
//            log.error("Twitter exception occurred", e);
//        }
//
//    }


    /**
     * Delete normal tweets sent by the users that match the delta requirements.
     * (ie. delete tweets older then 2 weeks. Default )
     */
    public void deleteTweets() {
        try {
            boolean found = true;
            int start = 1;
            int increment = twitterConfig.getIncrementCount();
            while (found) {
                found = false;
                List<Status> status = twitter.getUserTimeline(new Paging(start, start + increment));
                for (Status s : status) {
                    if (earlier.getTime() > s.getCreatedAt().getTime()) {
                        found = true;
                        twitter.destroyStatus(s.getId());
                        log.debug("removing tweeit with id: {}", s.getId());
                    }
                }
                log.debug("size of status batch is: {} ", status.size());
            }
        } catch (TwitterException e) {
            log.error("Twitter exception occurred", e);
        }

    }

    @Override
    public boolean execute() {
//        Twitter main = new Twitter();
        log.info("WARNING!!!!!!  Once authorized, this will delete EVERY tweet you ever posted, that is older then 2 weeks.\n" +
            "Do not continue unless you are absolutely certain you want to do this.");
        log.info("\n\nNote: Twitter rate limits us to 350 actions per hour.  If your twitter stream is very active, this will " +
            "most likely break and you'll need to re-run this application a few times over to do a proper cleanup.");
        Scanner scanner = new Scanner(System.in);
        log.info("Do you wish to continue? (y/n)?");
        String ans = scanner.nextLine();

        if (ans.toLowerCase().charAt(0) != 'y') {
            log.info("Exciting application....");
            System.exit(0);
        }

        AccessToken token = requestToken();
        twitter.setOAuthAccessToken(token);

        TwitterConfig config = twitterConfig;

        if (config.isCleanDirectMessages()) {
            deleteDirectMessages();
        }
        if (config.isCleanLists()) {
            removeLists();
        }
        if (config.isCleanTweets()) {
            deleteTweets();
        }
        if (config.isCleanFavorites()) {
            removeFavorites();
        }
        if (config.isCleanReTweets()) {
            removeRetweets();
        }
        if (config.isCleanFriendships()) {
            removeFriendships();
        }

        return true;
    }
}
