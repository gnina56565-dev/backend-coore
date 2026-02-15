package ru.mentee.power.crm.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @GetMapping("/leads/new")
    public String showCreateForm(Model model) {
        model.addAttribute("lead", new Lead(null, "", "", LeadStatus.NEW));
        return "leads/create";
    }

    @PostMapping("/leads")
    public String createLead(@ModelAttribute Lead lead) {
        leadService.addLead(lead.getEmail(), lead.getCompany(), lead.getStatus());
        return "redirect:/leads";
    }

    @GetMapping("/leads")
    public String showLeads(
            @RequestParam(required = false) LeadStatus status,
            Model model) {
        List<Lead> leads;
        if (status == null) {
            leads = leadService.findAll();
        } else {
            leads = leadService.findByStatus(status);
        }
        model.addAttribute("leads", leads);
        model.addAttribute("currentFilter", status);
        return "spring/list";
    }

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "Spring Boot CRM is running! Beans created: " + leadService.findAll().size() + " leads.";
    }

    @GetMapping("/leads/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model) {
        Lead lead = leadService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lead not found with id: " + id
                ));
        model.addAttribute("lead", lead);
        return "spring/edit";
    }

    @PostMapping("/leads/{id}")
    public String updateLead(@PathVariable UUID id, @ModelAttribute Lead lead) {
        leadService.update(id, lead);
        return "redirect:/leads";
    }

    @PostMapping("/leads/{id}/delete")
    public String deleteLead(@PathVariable UUID id) {
        leadService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,  // 404 — ресурс не найден
                        "Lead not found with id: " + id
                ));
        leadService.delete(id);
        return "redirect:/leads";
    }
}
