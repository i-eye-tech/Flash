package com.ieye;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FlashApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlashApplication.class, args);
    }

}
