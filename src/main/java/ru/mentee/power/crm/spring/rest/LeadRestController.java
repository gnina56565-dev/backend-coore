package ru.mentee.power.crm.spring.rest;

import jakarta.persistence.EntityNotFoundException;
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
import ru.mentee.power.crm.spring.dto.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.LeadResponse;
import ru.mentee.power.crm.spring.dto.UpdateLeadRequest;
import ru.mentee.power.crm.spring.mapper.LeadMapper;
import ru.mentee.power.crm.spring.service.LeadService;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/leads")
public class LeadRestController {

  private final LeadService leadService;
  private final LeadMapper leadMapper;

  public LeadRestController(LeadService leadService, LeadMapper leadMapper) {
    this.leadService = leadService;
    this.leadMapper = leadMapper;
  }

  @GetMapping
  public ResponseEntity<List<LeadResponse>> getAllLeads() {
    List<Lead> leads = leadService.getAllLeads();
    List<LeadResponse> responses = leads.stream().map(leadMapper::toResponse).toList();
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<LeadResponse> getLeadById(@PathVariable UUID id) {
    Optional<Lead> lead = leadService.findById(id);
    LeadResponse response = lead.map(leadMapper::toResponse)
        .orElseThrow(() -> new EntityNotFoundException("Lead not found with id: " + id));
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<LeadResponse> createLead(@RequestBody CreateLeadRequest request) {
    Lead lead = leadMapper.toEntity(request);
    Lead savedLead = leadService.save(lead);
    LeadResponse response = leadMapper.toResponse(savedLead);

    URI location = URI.create("/api/leads/" + savedLead.getId());
    return ResponseEntity.created(location).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<LeadResponse> updateLead(@PathVariable UUID id, @RequestBody UpdateLeadRequest request) {
    Lead lead = leadService.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Lead not found with id: " + id));

    leadMapper.updateEntity(request, lead);

    Lead updatedLead = leadService.save(lead);

    LeadResponse response = leadMapper.toResponse(updatedLead);
    return ResponseEntity.ok(response);
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
