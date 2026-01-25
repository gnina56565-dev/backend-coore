package ru.mentee.power.crm.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "ru.mentee.power.crm.spring.controller",  // ← LeadController!
        "ru.mentee.power.crm.spring.service",     // ← LeadService!
        "ru.mentee.power.crm.spring.repository"   // ← LeadRepository!
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
