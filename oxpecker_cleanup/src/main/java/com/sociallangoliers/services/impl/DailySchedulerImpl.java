package com.sociallangoliers.services.impl;

import akka.actor.ActorSystem;
import com.google.common.collect.Maps;
import com.sociallangoliers.repository.SocialAppRepository;
import com.sociallangoliers.services.PruneSocial;
import com.sociallangoliers.services.Scheduler;
import com.sociallangoliers.support.SocialCode;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Scanner;

@Service
@Log4j2
public class DailySchedulerImpl implements Scheduler {
    private final ActorSystem actorSystem;
    private final SocialAppRepository socialAppRepository;
    private final Map<SocialCode, PruneSocial> socialServices = Maps.newHashMap();


    @Autowired
    public DailySchedulerImpl(
        @Qualifier("twitter") PruneSocial twitter,
        ActorSystem actorSystem, SocialAppRepository socialAppRepository) {
        this.actorSystem = actorSystem;
        this.socialAppRepository = socialAppRepository;
        socialServices.put(SocialCode.twitter, twitter);
    }




    @Override
    public void runnow() {

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
    public PruneSocial instantiate(int choice) {
        SocialCode oChoice = null;
        SocialCode[] possibleValues = SocialCode.facebook.getDeclaringClass().getEnumConstants();
        for (SocialCode value : possibleValues) {
            if (value.ordinal() == choice) {
                oChoice = value;
                break;
            }
        }
        if (socialServices.containsKey(oChoice)) {
            PruneSocial ins = socialServices.get(oChoice);
            if (ins != null) {
                return ins;
            }
        }
        try {
            PruneSocial ins = (PruneSocial) Class.forName("com.sociallangoliers.services.impl." + WordUtils.capitalize(oChoice.name())).newInstance();
            socialServices.put(oChoice, ins);
            return ins;
        } catch (Exception e) {
            log.error("Failed to instantiate {}", oChoice, e);
        }
        return null;
    }

    /**
     * method will read the user's choice given by printMenu and execute the instantiated object
     */
    public void processChoices() {

        while (true) {
            int choice = printMenu();
            PruneSocial obj = instantiate(choice);
            if (obj == null) {
                log.error("Could not instantiate object for choice entered.  Exiting...");
                System.exit(choice);
            }
            obj.execute();
        }

    }

}
