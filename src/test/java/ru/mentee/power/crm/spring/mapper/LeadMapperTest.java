package ru.mentee.power.crm.spring.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.dto.generated.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GeneratedLeadMapperTest {

  private final GeneratedLeadMapper leadMapper = Mappers.getMapper(GeneratedLeadMapper.class);

  @Test
  void shouldMapCreateRequestToEntity_whenValidData() {
    CreateLeadRequest request = new CreateLeadRequest();
    request.setEmail("test@example.com");
    request.setCompany("Test Company");

    Lead lead = leadMapper.toEntity(request);

    assertThat(lead).isNotNull();
    assertThat(lead.getId()).isNull();
    assertThat(lead.getStatus()).isNull();
    assertThat(lead.getCompany()).isNull();
    assertThat(lead.getCreatedAt()).isNull();

    assertThat(lead.getEmail()).isEqualTo("test@example.com");
    assertThat(lead.getCompanyName()).isEqualTo("Test Company");
  }

  @Test
  void shouldMapEntityToResponse_whenValidEntity() {
    UUID testId = UUID.randomUUID();
    Company company = new Company("Mapped Corp", null);

    Lead lead = new Lead("mapped@example.com", company, LeadStatus.NEW);
    lead.setId(testId);
    LocalDateTime localCreatedAt = LocalDateTime.now();
    lead.setCreatedAt(localCreatedAt);

    LeadResponse response = leadMapper.toResponse(lead);

    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(testId);
    assertThat(response.getEmail()).isEqualTo("mapped@example.com");
    assertThat(response.getCompany()).isEqualTo("Mapped Corp");
    assertThat(response.getFirstName()).isNull();
    assertThat(response.getLastName()).isNull();
    assertThat(response.getCreatedAt()).isNotNull();
  }
}
