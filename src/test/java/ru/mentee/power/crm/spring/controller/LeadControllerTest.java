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
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
        Company company = new Company("Company1", "Industry");
        Lead lead = new Lead("test1@example.com", company, LeadStatus.NEW);

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
        Company company1 = new Company("Test Corp", "IT");
        Company company2 = new Company("Another Corp", "Finance");

        Lead ivanByNameObj = new Lead("ivan@test.com", company1, LeadStatus.NEW);
        ivanByNameObj.setId(UUID.randomUUID());

        Lead ivanByEmailObj = new Lead("contact@ivan-mail.ru", company2, LeadStatus.QUALIFIED);
        ivanByEmailObj.setId(UUID.randomUUID());

        when(leadService.findLeads(eq("ivan"), isNull()))
                .thenReturn(List.of(ivanByNameObj, ivanByEmailObj));

        mockMvc.perform(get("/leads").param("search", "ivan"))
                .andExpect(model().attribute("leads", List.of(ivanByNameObj, ivanByEmailObj)));
    }

    @Test
    void shouldReturnOnlyStatusNew() throws Exception {
        Company company = new Company("Test Corp", "IT");
        Lead ivanNew = new Lead("ivan@test.com", company, LeadStatus.NEW);
        ivanNew.setId(UUID.randomUUID());

        when(leadService.findLeads(isNull(), eq("NEW"))).thenReturn(List.of(ivanNew));

        mockMvc.perform(get("/leads").param("status", "NEW"))
                .andExpect(model().attribute("status", "NEW"))
                .andExpect(model().attribute("leads", List.of(ivanNew)));
    }

    @Test
    void shouldReturnLeadsWithoutParameters() throws Exception {
        Company company1 = new Company("Corp 1", "IT");
        Company company2 = new Company("Corp 2", "Finance");

        Lead ivanNew = new Lead("ivan@test.com", company1, LeadStatus.NEW);
        ivanNew.setId(UUID.randomUUID());

        Lead ivanQualified = new Lead("contact@ivan-mail.ru", company2, LeadStatus.QUALIFIED);
        ivanQualified.setId(UUID.randomUUID());

        when(leadService.findLeads(isNull(), isNull())).thenReturn(List.of(ivanNew, ivanQualified));

        mockMvc.perform(get("/leads"))
                .andExpect(model().attribute("leads", List.of(ivanNew, ivanQualified)))
                .andExpect(model().attribute("search", ""));
    }

    @Test
    void shouldFilterLeadsBySearchAndStatusCombined() throws Exception {
        Company company = new Company("Test Corp", "IT");
        Lead testUserNew = new Lead("test@example.com", company, LeadStatus.NEW);
        testUserNew.setId(UUID.randomUUID());

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

    @Test
    void shouldNullCompany() throws Exception {
        mockMvc.perform(post("/leads/new")
                                .param("email", "test@test.com")
                )
                .andExpect(model().attributeHasFieldErrors("lead", "company"));
    }

    @Test
    void shouldInvalidEmail() throws Exception {
        mockMvc.perform(post("/leads/new")
                        .param("email", "invalidemail")
                        .param("company", "Test Company")
                        .param("status", "NEW"))
                .andExpect(model().attributeHasFieldErrorCode("lead", "email", "Email"));
    }

    @Test
    void createLead_withValidData_shouldSaveAndRedirect() throws Exception {
        mockMvc.perform(post("/leads/new")
                        .param("name", "John")
                        .param("email", "john@test.com")
                        .param("company", "Test Company")
                        .param("status", "NEW"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/leads"));
    }
}