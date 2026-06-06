package ru.mentee.power.crm.spring.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.dto.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.LeadResponse;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LeadMapperTest {

  private final LeadMapper leadMapper = Mappers.getMapper(LeadMapper.class);

  @Test
  void shouldMapCreateRequestToEntity_whenValidData() {
    CreateLeadRequest request = new CreateLeadRequest();
    Lead lead = leadMapper.toEntity(request);

    assertThat(lead).isNotNull();

    assertThat(lead.getId()).isNull();

    assertThat(lead.getEmail()).isEqualTo("test@example.com");
    assertThat(lead.getCompanyName()).isEqualTo("Test Company");
    assertThat(lead.getStatus()).isNull();
    assertThat(lead.getCompany()).isNull();
    assertThat(lead.getCreatedAt()).isNull();
  }

  @Test
  void shouldMapEntityToResponse_whenValidEntity() {
    UUID testId = UUID.randomUUID();
    Company company = new Company("Mapped Corp", null);

    Lead lead = new Lead("mapped@example.com", company, LeadStatus.NEW);
    lead.setId(testId);
    lead.setCreatedAt(LocalDateTime.now());
    LeadResponse response = leadMapper.toResponse(lead);

    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(testId);
    assertThat(response.email()).isEqualTo("mapped@example.com");
    assertThat(response.company()).isEqualTo("Mapped Corp");
  }
}
