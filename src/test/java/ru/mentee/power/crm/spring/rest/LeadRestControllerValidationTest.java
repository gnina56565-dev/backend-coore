package ru.mentee.power.crm.spring.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.dto.CreateLeadRequest;
import ru.mentee.power.crm.spring.mapper.LeadMapper;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeadRestController.class)
class LeadRestControllerValidationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private LeadService leadService;

  @MockitoBean
  private LeadMapper leadMapper;

  @Test
  void shouldReturn400_whenEmailIsBlank() throws Exception {
    CreateLeadRequest request = new CreateLeadRequest();
    request.setEmail("");
    request.setFirstName("John");
    request.setLastName("Doe");

    String requestJson = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400_whenEmailIsInvalidFormat() throws Exception {
    CreateLeadRequest request = new CreateLeadRequest();
    request.setEmail("gmail.com");
    request.setFirstName("John");
    request.setLastName("Doe");

    String requestJson = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400_whenFirstNameIsTooShort() throws Exception {
    CreateLeadRequest request = new CreateLeadRequest();
    request.setEmail("john.doe@gmail.com");
    request.setFirstName("J");
    request.setLastName("Doe");

    String requestJson = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn201_whenAllFieldsAreValid() throws Exception {
    CreateLeadRequest request = new CreateLeadRequest();
    request.setEmail("johndoegm@gmail.com");
    request.setFirstName("John");
    request.setLastName("Doe");

    Lead lead = Lead.builder().email(request.getEmail()).status(LeadStatus.NEW).build();
    lead.setId(UUID.randomUUID());

    given(leadMapper.toEntity(request)).willReturn(lead);
    given(leadService.save(Mockito.any(Lead.class))).willReturn(lead);

    String requestJson = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isCreated());
  }
}
