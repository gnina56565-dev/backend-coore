package ru.mentee.power.crm.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.spring.service.DealService;
import ru.mentee.power.crm.spring.service.LeadService;

import java.math.BigDecimal;
import java.util.UUID;

@Controller
@RequestMapping("/deals")
public class DealController {
    private final DealService dealService;
    private final LeadService leadService;

    public DealController(DealService dealService, LeadService leadService) {
        this.dealService = dealService;
        this.leadService = leadService;
    }

    @GetMapping
    public String listDeals(Model model) {
        model.addAttribute("deals", dealService.getAllDeals());
        return "deals/list";
    }

    @GetMapping("/kanban")
    public String kanbanView(Model model) {
        model.addAttribute("dealsByStatus", dealService.getDealsByStatusForKanban());
        return "deals/kanban";
    }

    @GetMapping("/convert/{leadId}")
    public String showConvertForm(@PathVariable UUID leadId, Model model) {
        model.addAttribute("lead", leadService.findById(leadId));
        return "deals/convert";
    }

    @PostMapping("/convert")
    public String convertLeadToDeal(@RequestParam UUID leadId, @RequestParam BigDecimal amount) {
        dealService.convertLeadToDeal(leadId, amount);
        return "redirect:/deals";
    }

    @PostMapping("/{id}/transition")
    public String transitionStatus(@PathVariable UUID id, @RequestParam DealStatus newStatus) {
        dealService.transitionDealStatus(id, newStatus);
        return "redirect:/deals/kanban";
    }
}