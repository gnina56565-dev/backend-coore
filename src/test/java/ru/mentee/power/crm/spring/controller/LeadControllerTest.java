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
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    void shouldReturnIvanInEmailOrName() throws Exception {
        Lead ivanByName = new Lead(UUID.randomUUID(), "Ivan Petrov",
                "ivan@test.com", LeadStatus.NEW);
        Lead ivanByEmail = new Lead(UUID.randomUUID(), "Petr Ivanov",
                "contact@ivan-mail.ru", LeadStatus.QUALIFIED);
        when(leadService.findLeads(eq("ivan"), isNull()))
                .thenReturn(List.of(ivanByName, ivanByEmail));
        mockMvc.perform(get("/leads").param("search", "ivan"))
                .andExpect(model().attribute("leads", List.of(ivanByName, ivanByEmail)));
    }
    @Test
    void shouldReturnOnlyStatusNew() throws Exception {
        Lead ivanNew = new Lead(UUID.randomUUID(), "Ivan Petrov",
                "ivan@test.com", LeadStatus.NEW);
            when(leadService.findLeads(isNull(), eq("NEW"))).thenReturn(List.of(ivanNew));
        mockMvc.perform(get("/leads").param("status", "NEW"))
                .andExpect(model().attribute("status", "NEW"))
                .andExpect(model().attribute("leads", List.of(ivanNew)));
    }
    @Test
    void shouldReturnLeadsWithoutParameters() throws Exception {
        Lead ivanNew = new Lead(UUID.randomUUID(), "Ivan Petrov",
                "ivan@test.com", LeadStatus.NEW);
        Lead ivanQualified = new Lead(UUID.randomUUID(), "Petr Ivanov",
                "contact@ivan-mail.ru", LeadStatus.QUALIFIED);
        when(leadService.findLeads(isNull(), isNull())).thenReturn(List.of(ivanNew, ivanQualified));
        mockMvc.perform(get("/leads"))
                .andExpect(model().attribute("leads", List.of(ivanNew, ivanQualified)))
                .andExpect(model().attribute("search", ""));
    }
    @Test
    void shouldFilterLeadsBySearchAndStatusCombined() throws Exception {
        Lead testUserNew = new Lead(
                UUID.randomUUID(),
                "Test User",
                "test@example.com",
                LeadStatus.NEW
        );
        List<Lead> expected = List.of(testUserNew);

        when(leadService.findLeads(eq("test"), eq("NEW"))).thenReturn(expected);

        mockMvc.perform(get("/leads")
                        .param("search", "test")
                        .param("status", "NEW"))
                .andExpect(view().name("spring/list"))
                .andExpect(model().attribute("leads", expected))
                .andExpect(model().attribute("search", "test"))
                .andExpect(model().attribute("status", "NEW"));
    }
}