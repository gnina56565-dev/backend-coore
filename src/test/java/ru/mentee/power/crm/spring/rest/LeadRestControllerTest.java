package ru.mentee.power.crm.spring.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.dto.generated.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;
import ru.mentee.power.crm.spring.mapper.GeneratedLeadMapper;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeadRestController.class)
class GeneratedLeadRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private LeadService leadService;

  @MockitoBean
  private GeneratedLeadMapper generatedLeadMapper;

  @Test
  void shouldReturn200_whenGetAllLeads() throws Exception {
    UUID id = UUID.fromString("10000000-0000-0000-0000-000000000001");
    Company company = new Company("Test Corp", "IT");
    Lead lead = Lead.builder().id(id).email("john@example.com").company(company).status(LeadStatus.NEW).build();

    given(leadService.findAll()).willReturn(List.of(lead));

    LeadResponse expectedResponse = new LeadResponse(id, "john@example.com", "Test Corp", null, null);
    given(generatedLeadMapper.toResponse(lead)).willReturn(expectedResponse);

    mockMvc.perform(get("/api/leads").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].email").value("john@example.com"));
  }

  @Test
  void shouldReturn404_whenGetNonExistentLead() throws Exception {
    UUID nonExistentId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    given(leadService.findById(nonExistentId)).willReturn(java.util.Optional.empty());

    mockMvc.perform(get("/api/leads/{id}", nonExistentId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn201WithLocation_whenCreateLead() throws Exception {
    UUID createdId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
    String email = "new@example.com";
    String companyName = "New Corp";

    CreateLeadRequest requestDto = new CreateLeadRequest();
    requestDto.setEmail(email);
    requestDto.setCompany(companyName);

    Company company = new Company(companyName, "IT");
    Lead createdLead = Lead.builder().id(createdId).email(email).company(company).status(LeadStatus.NEW).build();

    LeadResponse responseDto = new LeadResponse(createdId, email, companyName, null, null);

    given(generatedLeadMapper.toEntity(requestDto)).willReturn(createdLead);

    given(leadService.save(createdLead)).willReturn(createdLead);

    given(generatedLeadMapper.toResponse(createdLead)).willReturn(responseDto);

    String requestJson = objectMapper.writeValueAsString(requestDto);

    mockMvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isCreated()).andExpect(header().exists("Location"))
        .andExpect(header().string("Location", "/api/leads/" + createdId));
  }

  @Test
  void shouldReturn204_whenDeleteExistingLead() throws Exception {
    UUID existingId = UUID.fromString("10000000-0000-0000-0000-000000000001");

    Company company = new Company("Test Corp", "IT");
    Lead existingLead = Lead.builder().id(existingId).email("existing@example.com").company(company)
        .status(LeadStatus.NEW).build();
    given(leadService.findById(existingId)).willReturn(java.util.Optional.of(existingLead));

    mockMvc.perform(delete("/api/leads/{id}", existingId)).andExpect(status().isNoContent());
  }

  @Test
  void shouldReturn404_whenDeleteNonExistentLead() throws Exception {
    UUID nonExistentId = UUID.fromString("00000000-0000-0000-0000-000000000000");

    given(leadService.findById(nonExistentId)).willReturn(java.util.Optional.empty());

    mockMvc.perform(delete("/api/leads/{id}", nonExistentId)).andExpect(status().isNotFound());
  }
}
