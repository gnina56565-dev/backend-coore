package ru.mentee.power.crm.spring.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.spring.service.DealService;
import ru.mentee.power.crm.spring.service.LeadService;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DealControllerTest {

    @Mock
    private DealService dealService;

    @Mock
    private LeadService leadService;

    @Mock
    private org.springframework.ui.Model model;

    private DealController controller;

    @BeforeEach
    void setUp() {
        controller = new DealController(dealService, leadService);
    }

    @Test
    void listDeals_ShouldAddDealsToModelAndReturnView() {
        List<Deal> deals = Arrays.asList(
                new Deal(UUID.randomUUID(), BigDecimal.TEN),
                new Deal(UUID.randomUUID(), BigDecimal.valueOf(100))
        );
        when(dealService.getAllDeals()).thenReturn(deals);

        String view = controller.listDeals(model);

        assertThat(view).isEqualTo("deals/list");
        verify(model).addAttribute("deals", deals);
        verify(dealService).getAllDeals();
    }

    @Test
    void kanbanView_ShouldAddDealsByStatusToModelAndReturnView() {
        Map<DealStatus, List<Deal>> dealsByStatus = Map.of(
                DealStatus.NEW, Arrays.asList(new Deal(UUID.randomUUID(), BigDecimal.TEN))
        );
        when(dealService.getDealsByStatusForKanban()).thenReturn(dealsByStatus);

        String view = controller.kanbanView(model);

        assertThat(view).isEqualTo("deals/kanban");
        verify(model).addAttribute("dealsByStatus", dealsByStatus);
        verify(dealService).getDealsByStatusForKanban();
    }

    @Test
    void showConvertForm_WhenLeadExists_ShouldAddLeadToModelAndReturnView() {
        UUID leadId = UUID.randomUUID();
        Lead lead = new Lead("test@example.com", null, ru.mentee.power.crm.model.LeadStatus.NEW);

        when(leadService.findById(leadId)).thenReturn(Optional.of(lead));

        String view = controller.showConvertForm(leadId, model);

        assertThat(view).isEqualTo("deals/convert");
        verify(model).addAttribute("lead", Optional.of(lead));
        verify(leadService).findById(leadId);
    }

    @Test
    void showConvertForm_WhenLeadNotFound_ShouldAddEmptyOptionalAndReturnView() {
        UUID leadId = UUID.randomUUID();

        when(leadService.findById(leadId)).thenReturn(Optional.empty());

        String view = controller.showConvertForm(leadId, model);

        assertThat(view).isEqualTo("deals/convert");
        verify(model).addAttribute("lead", Optional.empty());
        verify(leadService).findById(leadId);
    }

    @Test
    void convertLeadToDeal_ShouldCallServiceAndRedirect() {
        UUID leadId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(5000);

        String view = controller.convertLeadToDeal(leadId, amount);

        assertThat(view).isEqualTo("redirect:/deals");
        verify(dealService).convertLeadToDeal(leadId, amount);
    }

    @Test
    void transitionStatus_ShouldCallServiceAndRedirectToKanban() {
        UUID dealId = UUID.randomUUID();
        DealStatus newStatus = DealStatus.NEW;

        String view = controller.transitionStatus(dealId, newStatus);

        assertThat(view).isEqualTo("redirect:/deals/kanban");
        verify(dealService).transitionDealStatus(dealId, newStatus);
    }
}