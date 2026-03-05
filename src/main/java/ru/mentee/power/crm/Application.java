package ru.mentee.power.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "ru.mentee.power.crm")
@EntityScan(basePackages = "ru.mentee.power.crm.model")
@EnableJpaRepositories(basePackages = "ru.mentee.power.crm.repository")
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}