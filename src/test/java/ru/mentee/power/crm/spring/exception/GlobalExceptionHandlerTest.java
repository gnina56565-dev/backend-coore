package ru.mentee.power.crm.spring.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.spring.mapper.LeadMapper;
import ru.mentee.power.crm.spring.rest.LeadRestController;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeadRestController.class)
class GlobalExceptionHandlerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private LeadService service;

  @MockitoBean
  private LeadMapper leadMapper;

  @Test
  void shouldReturn404_whenEntityNotFound() throws Exception {
    UUID randomId = UUID.randomUUID();
    given(service.getLeadById(randomId)).willThrow(new EntityNotFoundException("Lead", randomId.toString()));
    mockMvc.perform(get("/api/leads/{id}", randomId)).andExpect(status().is4xxClientError())
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn400WithFieldErrors_whenValidationFails() throws Exception {
    String invalidJson = "{\"email\":\"\",\"firstName\":\"J\",\"lastName\":\"D\"}";

    mockMvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors").exists());
  }

  @Test
  void shouldReturn500_whenUnexpectedExceptionOccurs() throws Exception {
    UUID randomId = UUID.randomUUID();
    given(service.getLeadById(randomId)).willThrow(new RuntimeException("Unexpected error"));

    mockMvc.perform(get("/api/leads/{id}", randomId)).andExpect(status().is5xxServerError())
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
  }
}
