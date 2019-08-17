package com.sociallangoliers.concurency;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;

public class AwesomeActor {
   public static class Counter extends AbstractLoggingActor {
       private int counter = 0;
       //Protocol
      @Data
      @AllArgsConstructor
      public static class Message {
          private final String msg;
       };

       {
          receive();

       }

       public static Props props() {
           return Props.create(Counter.class);
       }

       private void onMessage(Message msg) {
            counter++;
            log().info("woot, counter value is: {}, msg is: {}", msg.getMsg(), counter);
       }

      @Override
      public Receive createReceive() {
          Receive receiveParam = ReceiveBuilder.create()
              .match(Message.class, this::onMessage).build();
          return receiveParam;
      }
   }

    public static void main(String[] args) {
        Props p = Counter.props();
        ActorSystem system = ActorSystem.create("moo");
        ActorRef counter = system.actorOf(p, "counter");
        Counter.Message m = new Counter.Message("moo");
        counter.tell(m, ActorRef.noSender());
        Counter.Message m2 = new Counter.Message("foobar");
        counter.tell(m2, ActorRef.noSender());
        for(int i=0; i < 5; i++ ) {
            new Thread(() -> {
                for(int j=0; j < 20; j++) {
                    counter.tell(new Counter.Message("moo " + j), ActorRef.noSender());
                }

            }).start();

        }
    }

}
