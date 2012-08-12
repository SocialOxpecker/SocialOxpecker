package com.sociallangoliers;


import com.sociallangoliers.config.spring.AppConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@Log4j2
@EnableTransactionManagement
@Import(AppConfig.class)
public class Main {


    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
