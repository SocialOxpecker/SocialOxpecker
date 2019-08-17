package com.sociallangoliers.concurency;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import lombok.Data;

public class AlarmActor {

    static class Alarm extends AbstractLoggingActor {
        //Protocol
        static class Activity {};
        @Data
        static class Disable{
            private final String password;
        };

        @Data
        static class Enable {
            private final String password;
        }

        private final String password;
        private final Receive  enabled;
        private final Receive disabled;

        public Alarm(String password) {
            this.password = password;

            enabled = ReceiveBuilder.create()
                .match(Activity.class, this::onActivity)
                .match(Disable.class, this::onDisable)
                .build();

            disabled= ReceiveBuilder.create()
                .match(Enable.class, this::onEnable).build();
        }

        private <P> void onEnable(Enable p) {
            if(p.password.equals(password)) {
                log().info("Enabled Alarm");
                getContext().become(enabled);
            } else {
                log().debug("INvalid password attempt on alarm enable");
            }
        }

        private void onDisable(Disable p) {
            if(p.password.equals(password)) {
                log().info("Disabled Alarm");
                getContext().become(disabled);
            } else {
                log().debug("INvalid password attempt on alarm disable");
            }
        }

        private void onActivity(Activity ignored) {
            log().warning("Sound Alarm");
        }

        @Override
        public Receive createReceive() {
            return disabled;
        }

        public static Props props(String cat) {
            return Props.create(Alarm.class, cat);
        }
    }


    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("main");
        ActorRef alarm = system.actorOf(Alarm.props("cat"), "alarm");

        alarm.tell(new Alarm.Activity(), ActorRef.noSender());
        alarm.tell(new Alarm.Enable("dogs"), ActorRef.noSender());
        alarm.tell(new Alarm.Enable("cat"), ActorRef.noSender());
        alarm.tell(new Alarm.Activity(), ActorRef.noSender());
        alarm.tell(new Alarm.Disable("dogs"), ActorRef.noSender());
        alarm.tell(new Alarm.Disable("cats"), ActorRef.noSender());
        system.terminate();

    }
}
