package com.example.poidetection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PoidetectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PoidetectionApplication.class, args);
    }
}