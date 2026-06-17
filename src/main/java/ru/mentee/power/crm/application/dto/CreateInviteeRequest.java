package ru.mentee.power.crm.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateInviteeRequest(@NotBlank @Email String email, @NotBlank @Size(min = 2, max = 50) String firstName) {
  public String companyName() {
    return null;
  }
}
