package ru.mentee.power.crm.application.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface InviteeService {

  InviteeResponse create(CreateInviteeRequest request);
  InviteeResponse getById(UUID id);
  Page<InviteeResponse> getAll(Pageable pageable);
  InviteeResponse updateStatus(UUID id, String status);
  void deleteById(UUID id);
}
