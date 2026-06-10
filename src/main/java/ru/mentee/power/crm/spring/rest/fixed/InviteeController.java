package ru.mentee.power.crm.spring.rest.fixed;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.application.dto.CreateInviteeRequest;
import ru.mentee.power.crm.application.dto.InviteeResponse;
import ru.mentee.power.crm.application.dto.InviteeService;
import ru.mentee.power.crm.application.dto.UpdateInviteeStatusRequest;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/invitees")
public class InviteeController {

  private final InviteeService inviteeService;

  public InviteeController(InviteeService inviteeService) {
    this.inviteeService = inviteeService;
  }

  @GetMapping
  public ResponseEntity<Page<InviteeResponse>> getAllInvitees(@PageableDefault(size = 20) Pageable pageable) {
    Page<InviteeResponse> invitees = inviteeService.getAll(pageable);
    return ResponseEntity.ok(invitees);
  }

  @GetMapping("/{id}")
  public ResponseEntity<InviteeResponse> getById(@PathVariable UUID id) {
    InviteeResponse invitee = inviteeService.getById(id);
    return ResponseEntity.ok(invitee);
  }

  @PostMapping
  public ResponseEntity<InviteeResponse> create(@Valid @RequestBody CreateInviteeRequest request) {
    InviteeResponse created = inviteeService.create(request);
    URI location = URI.create("/api/invitees/" + created.id());
    return ResponseEntity.created(location).body(created);
  }

  @PutMapping("/{id}/status")
  public ResponseEntity<InviteeResponse> updateStatus(@PathVariable UUID id,
      @Valid @RequestBody UpdateInviteeStatusRequest request) {
    InviteeResponse updated = inviteeService.updateStatus(id, request.status());
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    inviteeService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
