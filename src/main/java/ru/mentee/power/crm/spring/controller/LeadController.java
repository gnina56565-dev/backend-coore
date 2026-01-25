package ru.mentee.power.crm.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.service.LeadService;  // ← ИСПРАВИТЕ: spring.service!

import java.util.List;

@Controller  // ✅ @Controller НЕ @RestController!
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping("/leads")
    public String showLeads(Model model) {
        List<Lead> leads = leadService.findAll();
        model.addAttribute("leads", leads);
        return "leads/list";  // ✅ JTE путь, НЕ redirect!
    }
}
