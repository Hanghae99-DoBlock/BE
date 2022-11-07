package com.sparta.doblock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DoBlockApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoBlockApplication.class, args);
    }

}
