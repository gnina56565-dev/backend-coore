package ru.mentee.power.crm.spring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeadController.class)
@Import(LeadControllerTest.LeadServiceMockConfig.class)
class LeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LeadService leadService;

    @TestConfiguration
    static class LeadServiceMockConfig {
        @Bean
        @Primary
        public LeadService leadService() {
            return mock(LeadService.class);
        }
    }
    @Test
    void shouldDeleteLeadAndRedirect() throws Exception {
        UUID id = UUID.randomUUID();
        Lead lead = new Lead(); // Создаём тестовый лид
        when(leadService.findById(id)).thenReturn(Optional.of(lead));
        doNothing().when(leadService).delete(id);
        mockMvc.perform(post("/leads/{id}/delete", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/leads"));

        verify(leadService).delete(id);
    }

    @Test
    void shouldReturn404WhenLeadNotFound() throws Exception {
        UUID nonexistendId = UUID.randomUUID();
        when(leadService.findById(nonexistendId)).thenReturn(Optional.empty());
        mockMvc.perform(post("/leads/{id}/delete", nonexistendId))
                .andExpect(status().isNotFound());
        verify(leadService, never()).delete(nonexistendId);
    }
}