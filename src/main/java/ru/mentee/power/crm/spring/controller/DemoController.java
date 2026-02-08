package ru.mentee.power.crm.spring.controller;

import ru.mentee.power.crm.spring.service.LeadService;
import ru.mentee.power.crm.spring.repository.LeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DemoController {

    // Constructor Injection (recommended) — final field
    private final LeadService constructorService;

    // Field Injection (not recommended) — cannot be final
    @Autowired
    private LeadRepository fieldRepository;

    private LeadService setterService;

    public DemoController(LeadService constructorService) {
        this.constructorService = constructorService;
    }

    @Autowired(required = false)
    public void setSetterService(LeadService setterService) {
        this.setterService = setterService;
    }

    @GetMapping("/demo")
    @ResponseBody
    public String demo() {
        StringBuilder sb = new StringBuilder("DI Types Demo:\\n\\n");

        sb.append("Constructor Injection (final): ")
                .append(constructorService != null ? "✓ Injected" : "✗ NULL")
                .append("\\n");

        sb.append("Field Injection (@Autowired field): ")
                .append(fieldRepository != null ? "✓ Injected" : "✗ NULL")
                .append("\\n");

        sb.append("Setter Injection (@Autowired setter): ")
                .append(setterService != null ? "✓ Injected" : "✗ NULL")
                .append("\\n\\n");

        sb.append("Recommendation: Use Constructor Injection with final fields.");

        return sb.toString().replace("\\n", "<br>");
    }
}