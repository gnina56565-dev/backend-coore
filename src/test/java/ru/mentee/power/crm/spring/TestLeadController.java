package ru.mentee.power.crm.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestLeadController {

    @GetMapping("/health")
    public String health() {
        return "✅ Application is running! " + new java.util.Date();
    }

    @GetMapping("/test")
    public String test() {
        return "Test endpoint works!";
    }
}