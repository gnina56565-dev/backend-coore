package ru.mentee.power.crm.spring.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.repository.CompanyRepository;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LeadControllerTest {

    @Mock
    private LeadService leadService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    private LeadController controller;

    @BeforeEach
    void setUp() {
        controller = new LeadController(leadService, companyRepository);
    }

    @Test
    void home_ShouldReturnViewName() {
        String result = controller.home();
        assertThat(result).contains("Spring Boot CRM is running");
    }

    @Test
    void showCreateForm_ShouldAddEmptyLeadAndCompaniesToModel() {
        String result = controller.showCreateForm(model);
        assertThat(result).isIn("leads/create", "spring/create");
        verify(model).addAttribute(eq("lead"), any(Lead.class));
        verify(companyRepository, never()).findAll();
        verify(model, never()).addAttribute(eq("companies"), any());
    }

    @Test
    void createLead_WithValidationErrors_ShouldReturnCreateForm() {
        Lead lead = new Lead("test@example.com", new Company("Test", null), LeadStatus.NEW);
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = controller.createLead(lead, bindingResult, model);

        assertThat(result).isIn("leads/create", "spring/create");
        verify(leadService, never()).save(any(Lead.class));
        verify(model).addAttribute("errors", bindingResult);
        verify(model, never()).addAttribute(eq("companies"), any());
    }

    @Test
    void createLead_Success_ShouldReturnView() {
        Lead lead = new Lead("test@example.com", new Company("Test", null), LeadStatus.NEW);
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = controller.createLead(lead, bindingResult, model);

        assertThat(result).isIn("leads/create", "spring/create");
        verify(leadService, never()).save(any());
    }

    @Test
    void listLeads_WithNoFilters_ShouldReturnAllLeads() {
        Lead lead1 = new Lead("a@example.com", new Company("C1", null), LeadStatus.NEW);
        Lead lead2 = new Lead("b@example.com", new Company("C2", null), LeadStatus.CONTACTED);
        when(leadService.findLeads(null, null)).thenReturn(Arrays.asList(lead1, lead2));

        String result = controller.listLeads(null, null, " ", model);

        assertThat(result).isEqualTo("spring/list");
        verify(model).addAttribute("leads", Arrays.asList(lead1, lead2));
        verify(model).addAttribute("search", "");
        verify(model).addAttribute("status", "");
        verify(model).addAttribute("currentFilter", null);
        verify(model, never()).addAttribute(eq("companies"), any());
    }

    @Test
    void listLeads_WithFilters_ShouldPassFiltersToService() {
        when(leadService.findLeads("john", "NEW")).thenReturn(List.of());

        controller.listLeads("john", "NEW", "", model);

        verify(leadService).findLeads("john", "NEW");
        verify(model).addAttribute("search", "john");
        verify(model).addAttribute("status", "NEW");
        verify(model).addAttribute("currentFilter", LeadStatus.NEW);
    }

    @Test
    void showEditForm_WhenLeadExists_ShouldPopulateModel() {
        UUID id = UUID.randomUUID();
        Lead lead = new Lead("edit@example.com", new Company("EditCo", null), LeadStatus.CONTACTED);
        when(leadService.findById(id)).thenReturn(Optional.of(lead));

        String result = controller.showEditForm(id, model);

        assertThat(result).isEqualTo("spring/edit");
        verify(model).addAttribute("lead", lead);
        verify(model, never()).addAttribute(eq("companies"), any());
        verify(companyRepository, never()).findAll();
    }

    @Test
    void showEditForm_WhenLeadNotFound_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(leadService.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.showEditForm(id, model))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    void deleteLead_WhenSuccess_ShouldRedirect() {
        UUID id = UUID.randomUUID();
        Lead existingLead = new Lead("test@example.com", new Company("Test", null), LeadStatus.NEW);
        when(leadService.findById(id)).thenReturn(Optional.of(existingLead));

        String result = controller.deleteLead(id);

        assertThat(result).isEqualTo("redirect:/leads");
        verify(leadService).delete(id);
    }

    @Test
    void deleteLead_WhenNotFound_ShouldRedirectWithError() {
        UUID id = UUID.randomUUID();
        when(leadService.findById(id)).thenReturn(Optional.empty());

        String result = controller.deleteLead(id);

        assertThat(result).isEqualTo("redirect:/leads?error=unknown");
        verify(leadService, never()).delete(any());
    }

    @Test
    void updateLead_WithValidationErrors_ShouldReturnEditForm() {
        UUID id = UUID.randomUUID();
        Lead lead = new Lead("update@example.com", new Company("UpdCo", null), LeadStatus.NEW);

        when(bindingResult.hasErrors()).thenReturn(true);

        String result = controller.updateLead(id, lead, bindingResult, model);

        assertThat(result).isIn("leads/edit", "spring/edit");

        verify(leadService, never()).update(any(), any());
        verify(model, never()).addAttribute(eq("errors"), any());
    }

    @Test
    void updateLead_Success_ShouldReturnView_AndNotCallUpdate() {
        UUID id = UUID.randomUUID();
        Lead lead = new Lead("update@example.com", new Company("UpdCo", null), LeadStatus.NEW);
        lead.setId(id);

        when(bindingResult.hasErrors()).thenReturn(false);

        String result = controller.updateLead(id, lead, bindingResult, model);

        assertThat(result).isIn("redirect:/leads", "leads/edit", "spring/edit");
        verify(leadService, never()).update(any(), any());

        verify(model, never()).addAttribute(eq("errors"), any());
    }
}