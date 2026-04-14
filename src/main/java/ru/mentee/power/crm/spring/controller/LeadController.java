package ru.mentee.power.crm.spring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.repository.CompanyRepository;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;
    private final CompanyRepository companyRepository;

    @GetMapping("/leads/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("lead")) {
            Lead emptyLead = new Lead("", LeadStatus.NEW);
            model.addAttribute("lead", emptyLead);
        }
        return "leads/create";
    }

    @PostMapping("/leads/new")
    public String createLead(@Valid @ModelAttribute("lead") Lead lead,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            return "leads/create";
        }

        String companyName = lead.getCompanyName() != null ? lead.getCompanyName() : "";
        if (companyName.isBlank()) {
            bindingResult.rejectValue("companyName", "error.company", "Название компании обязательно");
            model.addAttribute("errors", bindingResult);
            return "leads/create";
        }

        try {
            leadService.addLead(lead.getEmail(), companyName, lead.getStatus());
            return "redirect:/leads";
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            handleDuplicateEmail(e, bindingResult, model);
            return "leads/create";
        } catch (Exception e) {
            bindingResult.reject("error.global", "Произошла непредвиденная ошибка при сохранении.");
            model.addAttribute("errors", bindingResult);
            return "leads/create";
        }
    }

    private void handleDuplicateEmail(org.springframework.dao.DataIntegrityViolationException e,
                                      BindingResult bindingResult,
                                      Model model) {
        Throwable cause = e.getRootCause();
        while (cause != null) {
            if (cause instanceof org.hibernate.exception.ConstraintViolationException ||
                    (cause.getMessage() != null && cause.getMessage().contains("duplicate key"))) {

                bindingResult.rejectValue("email", "error.email.duplicate", "Пользователь с таким Email уже существует!");
                model.addAttribute("errors", bindingResult);
                return;
            }
            cause = cause.getCause();
        }
        bindingResult.reject("error.db", "Lead с таким email уже существует.");
        model.addAttribute("errors", bindingResult);
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
        if (lead.getCompany() != null) {
            lead.setCompanyName(lead.getCompany().getName());
        }
        model.addAttribute("lead", lead);
        return "spring/edit";
    }

    @PostMapping("/leads/{id}")
    public String updateLead(@PathVariable UUID id,
                             @Valid @ModelAttribute Lead formLead,
                             BindingResult bindingResult,
                             Model model) {
        String companyName = formLead.getCompanyName();
        if (companyName == null || companyName.isBlank()) {
            bindingResult.rejectValue("companyName", "error.company.required", "Компания обязательна");
            return "spring/edit";
        }
        if (bindingResult.hasErrors()) {
            return "spring/edit";
        }

        try {
            Lead existingLead = leadService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
            existingLead.setEmail(formLead.getEmail());
            existingLead.setStatus(formLead.getStatus());
            Company company = companyRepository.findByName(companyName)
                    .orElseGet(() -> {
                        Company newCompany = new Company();
                        newCompany.setName(companyName);
                        return companyRepository.save(newCompany);
                    });
            existingLead.setCompany(company);
            leadService.save(existingLead);
            return "redirect:/leads";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Ошибка при сохранении: " + e.getMessage());
            model.addAttribute("lead", formLead);
            return "spring/edit";
        }
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