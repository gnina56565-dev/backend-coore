package ru.mentee.power.crm.spring.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.mapper.LeadMapper;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class LeadRestControllerTest {

  private MockMvc mockMvc;

  @Mock
  private LeadService leadService;

  @Mock
  private LeadMapper leadMapper;

  @BeforeEach
  void setUp() {
    LeadRestController leadRestController = new LeadRestController(leadService, leadMapper);
    mockMvc = MockMvcBuilders.standaloneSetup(leadRestController).build();
  }

  @Test
  void shouldReturn200_whenGetAllLeads() throws Exception {
    UUID id = UUID.fromString("10000000-0000-0000-0000-000000000001");
    Company company = new Company("Test Corp", "IT");
    Lead lead = new Lead(id, "john@example.com", company, LeadStatus.NEW);

    when(leadService.getAllLeads()).thenReturn(List.of(lead));

    mockMvc.perform(get("/api/leads").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].email").value("john@example.com"));
  }

  @Test
  void shouldReturn404_whenGetNonExistentLead() throws Exception {
    UUID nonExistentId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    when(leadService.getLeadById(nonExistentId)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/leads/{id}", nonExistentId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn201WithLocation_whenCreateLead() throws Exception {
    UUID createdId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
    Company company = new Company("New Corp", "IT");
    Lead createdLead = new Lead(createdId, "new@example.com", company, LeadStatus.NEW);

    when(leadService.createLead(any(Lead.class))).thenReturn(createdLead);

    String requestJson = """
        {
          "email": "new@example.com",
          "status": "NEW",
          "companyName": "New Corp"
        }
        """;

    mockMvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isCreated()).andExpect(header().exists("Location"))
        .andExpect(header().string("Location", "/api/leads/" + createdId));
  }

  @Test
  void shouldReturn204_whenDeleteExistingLead() throws Exception {
    UUID existingId = UUID.fromString("10000000-0000-0000-0000-000000000001");
    when(leadService.deleteLead(existingId)).thenReturn(true);

    mockMvc.perform(delete("/api/leads/{id}", existingId)).andExpect(status().isNoContent())
        .andExpect(content().string(""));
  }

  @Test
  void shouldReturn404_whenDeleteNonExistentLead() throws Exception {
    UUID nonExistentId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    when(leadService.deleteLead(nonExistentId)).thenReturn(false);

    mockMvc.perform(delete("/api/leads/{id}", nonExistentId)).andExpect(status().isNotFound());
  }
}
