package ru.mentee.power.crm.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.mentee.power.crm.spring.service.LeadService;

@Controller
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping("/leads")
    public String showLeads(Model model) {
        model.addAttribute("leads", leadService.findAll());
        return "spring/leads/list";
    }
}
