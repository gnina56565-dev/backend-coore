package ru.mentee.power.crm.application.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.exception.EntityNotFoundException;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteeServiceImpl implements InviteeService {

  private final LeadService leadService;
  private final InviteeMapper inviteeMapper;

  @Override
  public InviteeResponse create(CreateInviteeRequest request) {
    String companyNameStr = request.companyName();
    if (companyNameStr == null || companyNameStr.isBlank()) {
      throw new IllegalArgumentException("Company name is required for creating a lead.");
    }
    LeadStatus status = LeadStatus.NEW;
    leadService.addLead(request.email(), companyNameStr, status);

    Lead savedLead = leadService.findByEmail(request.email())
        .orElseThrow(() -> new IllegalStateException("Lead was not created correctly after addLead call."));

    return inviteeMapper.toResponse(savedLead);
  }

  @Override
  public InviteeResponse getById(UUID id) {
    Lead lead = leadService.findById(id).orElseThrow(() -> new EntityNotFoundException("Lead", id.toString()));
    return inviteeMapper.toResponse(lead);
  }

  @Override
  public Page<InviteeResponse> getAll(Pageable pageable) {
    if (pageable.getPageNumber() == 0) {
      Page<Lead> firstPage = leadService.getFirstPage(pageable.getPageSize());
      return firstPage.map(inviteeMapper::toResponse);
    } else {
      throw new UnsupportedOperationException(
          "Pagination beyond first page not supported in underlying LeadService via InviteeService");
    }
  }

  @Override
  public InviteeResponse updateStatus(UUID id, String statusStr) {
    LeadStatus leadStatus;
    try {
      leadStatus = LeadStatus.valueOf(statusStr.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid status: " + statusStr, e);
    }

    Lead existingLead = leadService.findById(id).orElseThrow(() -> new EntityNotFoundException("Lead", id.toString()));

    existingLead.setStatus(leadStatus);
    Lead updatedLead = leadService.save(existingLead);
    return inviteeMapper.toResponse(updatedLead);
  }

  @Override
  public void deleteById(UUID id) {
    Lead existingLead = leadService.findById(id).orElseThrow(() -> new EntityNotFoundException("Lead", id.toString()));
    leadService.delete(id);
  }
}
