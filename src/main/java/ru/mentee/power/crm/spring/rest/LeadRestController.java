package ru.mentee.power.crm.spring.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/leads")
public class LeadRestController {

  private final LeadService leadService;

  @GetMapping
  public List<Lead> getAllLeads() {
    return leadService.findAll();
  }

  @GetMapping("/{id}")
  public Lead getLeadById(@PathVariable UUID id) {
    return leadService.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Lead createLead(@RequestBody Lead lead) {
    return leadService.createLead(lead);
  }
}
