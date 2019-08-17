package com.sociallangoliers.services.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.sociallangoliers.actors.TwitterWorker;
import com.sociallangoliers.db_common.models.config.tables.pojos.SocialApp;
import com.sociallangoliers.db_common.models.config.tables.pojos.UserSocial;
import com.sociallangoliers.models.User;
import com.sociallangoliers.repository.SocialAppRepository;
import com.sociallangoliers.services.PruneSocial;
import com.sociallangoliers.services.Scheduler;
import com.sociallangoliers.services.SocialAdapter;
import com.sociallangoliers.services.UserService;
import com.sociallangoliers.support.SocialAction;
import com.sociallangoliers.support.SocialCode;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import twitter4j.auth.RequestToken;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Service
@Log4j2
public class DailySchedulerImpl implements Scheduler {
    private final ActorSystem actorSystem;
    private final SocialAppRepository socialAppRepository;
//    private final Map<SocialCode, PruneSocial> socialServices = Maps.newHashMap();
    private final UserService userService;
    Map<SocialCode, ActorRef> akkaActors = Maps.newHashMap();
    private final SocialAdapter socialAdapter;


    @Autowired
    public DailySchedulerImpl(
        final ActorSystem actorSystem,
        final SocialAppRepository socialAppRepository,
        final UserService userService,
        final SocialAdapter socialAdapter) {
        this.actorSystem = actorSystem;
        this.socialAdapter = socialAdapter;
        ActorRef twitterRef = this.actorSystem.actorOf(TwitterWorker.props(), "twitter");
        akkaActors.put(SocialCode.twitter, twitterRef);
        this.socialAppRepository = socialAppRepository;
        this.userService = userService;
    }


    @Override
    public void runnow() {
        //Get all user list.
        List<User> userListing = userService.getUserListing();
        for (final User user : userListing) {
            List<UserSocial> enabledSocialApps = user.getEnabledSocialApps();
            for (UserSocial userSocial : enabledSocialApps) {
                SocialApp socialApp = socialAppRepository.getSocialAppById(userSocial.getAppId());
                log.debug(socialApp);
                SocialCode socialCode = null;
                try {
                    socialCode = SocialCode.valueOf(socialApp.getSocialAppType().toLowerCase());
                } catch (Exception e) {
                    log.error("Unsupported social app detected: {}", socialApp.getSocialAppType());
                    continue;
                }
                //if match twitter
                ActorRef actorRef = akkaActors.get(socialCode);
                if (actorRef != null) {
                    //for each action, generate a different  message
                    JsonNode metaData = userSocial.getMetaData();
                    RequestToken appToken = new RequestToken(socialApp.getClientId(), userSocial.getSecretToken());
                    for (SocialAction action : SocialAction.values()) {
                        try {
                            Boolean actionEnabled = metaData.get(action.name()).booleanValue();
                            if (actionEnabled) {
                                actorRef.tell(
                                    TwitterWorker.TwitterMessageRequest.builder()
                                        .action(action)
                                        .consumer(userSocial.getAccessToken())
                                        .secret(userSocial.getSecretToken())
                                        .requestToken(appToken)
                                        .lookbackWindow(userSocial.getLookbackWindow())
                                        .socialPrune(socialAdapter.getSocialService(socialCode))
                                        .build(),
                                    ActorRef.noSender());

//                                    new TwitterWorker.TwitterMessageRequest(
//                                        userSocial.getAccessToken(), userSocial.getSecretToken(), action, userSocial.getLookbackWindow(), socialPrune),
                            }
                        } catch (Exception e) {
                            log.warn("Error, no value set for action in meta data object");
                        }

                    }

                }
            }
        }


    }


    // EVERTHING BELOW THIS LINE IS LEGACY

    /**
     * Uses reflection of enum to generate a menu and read in user choice.
     *
     * @return
     */
    private int printMenu() {
        log.info("Which social media service would you like to use? ");
        SocialCode[] possibleValues = SocialCode.facebook.getDeclaringClass().getEnumConstants();
        for (SocialCode value : possibleValues) {
            log.info(value.ordinal() + ". " + WordUtils.capitalize(value.name()));
        }
        log.info("99. Exit");
        System.out.print("What is your choice?  ==> ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        return choice;

    }

    /**
     * Give a numeric choice matching the enum, it will instantiate a corresponding object.
     *
     * @param choice
     * @return
     */
//    public PruneSocial instantiate(int choice) {
//        SocialCode oChoice = null;
//        SocialCode[] possibleValues = SocialCode.facebook.getDeclaringClass().getEnumConstants();
//        for (SocialCode value : possibleValues) {
//            if (value.ordinal() == choice) {
//                oChoice = value;
//                break;
//            }
//        }
//        if (socialServices.containsKey(oChoice)) {
//            PruneSocial ins = socialServices.get(oChoice);
//            if (ins != null) {
//                return ins;
//            }
//        }
//        try {
//            PruneSocial ins = (PruneSocial) Class.forName("com.sociallangoliers.services.impl." + WordUtils.capitalize(oChoice.name())).newInstance();
//            socialServices.put(oChoice, ins);
//            return ins;
//        } catch (Exception e) {
//            log.error("Failed to instantiate {}", oChoice, e);
//        }
//        return null;
//    }

    /**
     * method will read the user's choice given by printMenu and execute the instantiated object
     */
    public void processChoices() {

        while (true) {
            int choice = printMenu();
            SocialCode code = SocialCode.values()[choice];
            PruneSocial obj = socialAdapter.getSocialService(code);
            if (obj == null) {
                log.error("Could not instantiate object for choice entered.  Exiting...");
                System.exit(choice);
            }
            obj.execute();
        }

    }

}
