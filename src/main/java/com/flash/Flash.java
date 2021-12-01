package com.flash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableMongoRepositories
@EnableAsync
@EnableCaching
public class Flash {

    public static void main(String[] args) {
        SpringApplication.run(Flash.class, args);
    }

}
