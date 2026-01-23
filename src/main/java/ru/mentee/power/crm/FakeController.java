package ru.mentee.power.crm;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.service.LeadService;

import java.util.List;

@Controller
public class FakeController {

    private final LeadService leadService;

    public FakeController(LeadService leadService) {
        this.leadService = leadService;
    }
    @GetMapping("/leads")
    public String showLeads(Model model) {
        List<Lead> leads = leadService.findAll();
        model.addAttribute("leads", leads);
        return "leads/list";
    }
}