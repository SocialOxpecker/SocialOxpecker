package com.sociallangoliers.config.spring;

import akka.actor.ActorSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {


    @Bean
    public ActorSystem actorSystem() {
        return ActorSystem.create("social");
    }
}
