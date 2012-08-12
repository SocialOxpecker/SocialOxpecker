package com.sociallangoliers.concurency;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

class PrintMyActorRefActor extends AbstractActor {
    static Props props() {
        return Props.create(PrintMyActorRefActor.class, PrintMyActorRefActor::new);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .matchEquals(
                "printit",
                p -> {
                    ActorRef secondRef = getContext().actorOf(Props.empty(), "second-actor");
                    System.out.println("Second: " + secondRef);
                })
            .build();
    }
}

