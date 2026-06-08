package ru.mentee.power.crm.spring.rest;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.spring.dto.generated.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;
import ru.mentee.power.crm.spring.dto.generated.UpdateLeadRequest;
import ru.mentee.power.crm.spring.mapper.LeadMapper;
import ru.mentee.power.crm.spring.rest.generated.LeadManagementApi;
import ru.mentee.power.crm.spring.service.LeadService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
public class LeadRestController implements LeadManagementApi {

  private final LeadService leadService;
  private final LeadMapper leadMapper;

  public LeadRestController(LeadService leadService, LeadMapper leadMapper) {
    this.leadService = leadService;
    this.leadMapper = leadMapper;
  }

  @Override
  public ResponseEntity<List<LeadResponse>> getLeads() {
    List<Lead> lead = leadService.findAll();
    List<LeadResponse> responseList = lead.stream().map(leadMapper::toResponse).toList();
    return ResponseEntity.ok(responseList);
  }

  @Override
  public ResponseEntity<LeadResponse> createLead(CreateLeadRequest createLeadRequest) {
    Lead lead = leadMapper.toEntity(createLeadRequest);
    Lead saved = leadService.save(lead);
    LeadResponse response = leadMapper.toResponse(saved);
    URI location = URI.create("/api/leads/" + saved.getId());
    return ResponseEntity.created(location).body(response);
  }

  @Override
  public ResponseEntity<LeadResponse> getLeadById(UUID id) {
    Lead lead = leadService.findById(id).orElseThrow(() -> new EntityNotFoundException());
    LeadResponse response = leadMapper.toResponse(lead);
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<LeadResponse> updateLead(UUID id, UpdateLeadRequest updateLeadRequest) {
    Lead existingLead = leadService.findById(id).orElseThrow(() -> new EntityNotFoundException());

    leadMapper.updateEntity(updateLeadRequest, existingLead);
    Lead updatedLead = leadService.update(id, existingLead);
    LeadResponse response = leadMapper.toResponse(updatedLead);
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<Void> deleteLead(UUID id) {
    leadService.findById(id).orElseThrow(() -> new EntityNotFoundException());
    leadService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
