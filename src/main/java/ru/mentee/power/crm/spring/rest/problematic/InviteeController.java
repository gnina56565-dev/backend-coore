package ru.mentee.power.crm.spring.rest.problematic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/invitees")
public class InviteeController {

  @Autowired
  LeadService leadService;

  @PostMapping("/getInvitees")
  public List<Lead> getInvitees() {
    return leadService.findAll();
  }

  @GetMapping("/{id}")
  public Lead getById(@PathVariable UUID id) {
    Optional<Lead> optLead = leadService.findById(id);
    if (optLead.isPresent()) {
      return optLead.get();
    } else {
      throw new RuntimeException("Lead not found with id: " + id);
    }
  }

  @PostMapping
  public Lead create(@RequestBody Map<String, Object> params) {
    String email = (String) params.get("email");
    String companyName = (String) params.get("companyName");
    String statusStr = (String) params.get("status");

    if (email == null || !email.contains("@")) {
      throw new IllegalArgumentException("Invalid email: " + email);
    }

    try {
      leadService.addLead(email, companyName, LeadStatus.valueOf(statusStr != null ? statusStr : "NEW"));
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid status provided: " + statusStr, e);
    }

    return null;
  }

  @DeleteMapping("/{id}")
  public Lead delete(@PathVariable UUID id) {
    Optional<Lead> optLead = leadService.findById(id);
    if (optLead.isPresent()) {
      Lead lead = optLead.get();
      leadService.delete(id);
      return lead;
    } else {
      return null;
    }
  }

  @PutMapping("/{id}/status")
  public Lead updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
    try {
      Optional<Lead> optLead = leadService.findById(id);
      if (optLead.isEmpty()) {
        throw new RuntimeException("Lead not found with id: " + id);
      }
      Lead lead = optLead.get();
      String status = body.get("status");

      if (status == null) {
        throw new RuntimeException("Status is required");
      }
      LeadStatus newStatus;
      try {
        newStatus = LeadStatus.valueOf(status.toUpperCase());
      } catch (IllegalArgumentException e) {
        throw new RuntimeException("Invalid status provided: " + status);
      }

      LeadStatus currentStatus = lead.getStatus();
      if (currentStatus == LeadStatus.CONTACTED && newStatus == LeadStatus.NEW) {
        throw new IllegalStateException("Cannot revert status from CONTACTED to NEW");
      }

      lead.setStatus(newStatus);

      Lead updatedLead = leadService.save(lead);
      return updatedLead;
    } catch (Exception e) {
      return null;
    }
  }
}
