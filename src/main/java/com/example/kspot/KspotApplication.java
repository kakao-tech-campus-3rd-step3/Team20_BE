package com.example.kspot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KspotApplication {

  public static void main(String[] args) {
    SpringApplication.run(KspotApplication.class, args);
  }

}
