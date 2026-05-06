package ru.mentee.power.crm.spring.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.spring.service.LeadService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leads")
public class LeadRestController {
  private final LeadService leadService;

  public LeadRestController(LeadService leadService) {
    this.leadService = leadService;
  }

  @GetMapping
  public ResponseEntity<List<Lead>> getAllLeads() {

    List<Lead> leads = leadService.getAllLeads();
    return ResponseEntity.ok(leads);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Lead> getLeadById(@PathVariable UUID id) {
    return leadService.getLeadById(id).map(lead -> ResponseEntity.ok(lead)).orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Lead> createLead(@RequestBody Lead lead) {
    Lead createdLead = leadService.createLead(lead);
    URI location = URI.create("/api/leads/" + createdLead.getId());
    return ResponseEntity.created(location).body(createdLead);
  }
  @PutMapping("/{id}")
  public ResponseEntity<Lead> updateLead(@PathVariable UUID id, @RequestBody Lead lead) {
    ResponseEntity<Lead> updatedLead = leadService.updateLead(id, lead).map(updated -> ResponseEntity.ok(updated))
        .orElse(ResponseEntity.notFound().build());
    return updatedLead;
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteLead(@PathVariable UUID id) {
    boolean deleted = leadService.deleteLead(id);
    if (!deleted) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
  }
}
