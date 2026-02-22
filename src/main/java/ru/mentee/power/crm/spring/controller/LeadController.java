package ru.mentee.power.crm.spring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
        if (!model.containsAttribute("lead")){
            model.addAttribute("lead", new Lead());
        }
        return "leads/create";
    }

    @PostMapping("/leads/new")
    public String createLead(@Valid @ModelAttribute("lead") Lead lead,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "spring/leads/form";
        }
        leadService.save(lead);
        return "redirect:/leads";
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
    public String updateLead(@PathVariable UUID id,
                             @Valid @ModelAttribute("lead") Lead lead,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "spring/leads/form";
        }
        lead.setId(id);
        leadService.save(lead);
        return "redirect:/leads";
    }


    @PostMapping("/leads/{id}/delete")
    public String deleteLead(@PathVariable UUID id) {
        leadService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lead not found with id: " + id
                ));
        leadService.delete(id);
        return "redirect:/leads";
    }
    @GetMapping("/leads")
    public String listLeads(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            Model model) {
        List<Lead> leads = leadService.findLeads(search, status);
        model.addAttribute("leads", leads);
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("status", status != null ? status : "");
        model.addAttribute("currentFilter", status != null && !status.isBlank()
                ? LeadStatus.valueOf(status.toUpperCase())
                : null);
        return "spring/list";
    }
}
