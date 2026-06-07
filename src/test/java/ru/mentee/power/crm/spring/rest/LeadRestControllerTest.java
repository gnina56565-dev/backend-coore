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
import ru.mentee.power.crm.spring.dto.LeadResponse;
import ru.mentee.power.crm.spring.exception.EntityNotFoundException;
import ru.mentee.power.crm.spring.mapper.LeadMapper;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeadRestController.class)
class LeadRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private LeadService leadService;

  @MockitoBean
  private LeadMapper leadMapper;

  @Test
  void shouldReturn200_whenGetAllLeads() throws Exception {
    UUID id = UUID.fromString("10000000-0000-0000-0000-000000000001");
    Company company = new Company("Test Corp", "IT");
    Lead lead = Lead.builder().id(id).email("john@example.com").company(company).status(LeadStatus.NEW).build();

    given(leadService.getAllLeads()).willReturn(List.of(lead));
    given(leadMapper.toResponse(lead)).willReturn(new LeadResponse(id, "john@example.com", LeadStatus.NEW, null, null));

    mockMvc.perform(get("/api/leads").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].email").value("john@example.com"));
  }

  @Test
  void shouldReturn404_whenGetNonExistentLead() throws Exception {
    UUID nonExistentId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    given(leadService.getLeadById(nonExistentId))
        .willThrow(new EntityNotFoundException("Lead", nonExistentId.toString()));

    mockMvc.perform(get("/api/leads/{id}", nonExistentId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn201WithLocation_whenCreateLead() throws Exception {
    UUID createdId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
    Company company = new Company("New Corp", "IT");
    Lead createdLead = Lead.builder().id(createdId).email("new@example.com").company(company).status(LeadStatus.NEW)
        .build();

    given(leadMapper.toEntity(any())).willReturn(createdLead);
    given(leadService.save(any())).willReturn(createdLead);
    given(leadMapper.toResponse(createdLead))
        .willReturn(new LeadResponse(createdId, "new@example.com", LeadStatus.NEW, null, null));

    String requestJson = """
        {
          "email": "new@example.com",
          "firstName": "John",
          "lastName": "Doe",
          "company": "New Corp"
        }
        """;

    mockMvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isCreated()).andExpect(header().exists("Location"))
        .andExpect(header().string("Location", "/api/leads/" + createdId));
  }

  @Test
  void shouldReturn204_whenDeleteExistingLead() throws Exception {
    UUID existingId = UUID.fromString("10000000-0000-0000-0000-000000000001");

    mockMvc.perform(delete("/api/leads/{id}", existingId)).andExpect(status().isNoContent());
  }

  @Test
  void shouldReturn404_whenDeleteNonExistentLead() throws Exception {
    UUID nonExistentId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    willThrow(new EntityNotFoundException("Lead", nonExistentId.toString())).given(leadService)
        .deleteLead(nonExistentId);

    mockMvc.perform(delete("/api/leads/{id}", nonExistentId)).andExpect(status().isNotFound());
  }
}
