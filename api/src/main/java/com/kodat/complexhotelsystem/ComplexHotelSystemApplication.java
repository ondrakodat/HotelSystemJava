package com.kodat.complexhotelsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@SpringBootApplication(scanBasePackages = "com.kodat")
public class ComplexHotelSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ComplexHotelSystemApplication.class, args);
    }
}
