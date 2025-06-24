package com.holidayproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HolidayProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(HolidayProjectApplication.class, args);
    }

}
