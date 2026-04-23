package ru.mentee.power.crm.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.repository.DealRepository;
import ru.mentee.power.crm.spring.repository.LeadRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

	@Mock
	private DealRepository dealRepository;

	@Mock
	private LeadRepository leadRepository;

	private DealService dealService;

	@BeforeEach
	void setUp() {
		dealService = new DealService(dealRepository, leadRepository);
	}

	@Test
	void convertLeadToDeal_WhenLeadExists_ShouldCreateAndSaveDeal() {
		UUID leadId = UUID.randomUUID();
		BigDecimal amount = BigDecimal.valueOf(5000);
		Lead existingLead = new Lead("test@example.com", new Company("Test", null), LeadStatus.NEW);
		when(leadRepository.findById(leadId)).thenReturn(Optional.of(existingLead));

		when(dealRepository.save(any(Deal.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Deal result = dealService.convertLeadToDeal(leadId, amount);

		assertThat(result).isNotNull();
		assertThat(result.getLeadId()).isEqualTo(leadId);
		assertThat(result.getAmount()).isEqualTo(amount);
		assertThat(result.getStatus()).isEqualTo(DealStatus.NEW);

		verify(leadRepository).findById(leadId);
		verify(dealRepository).save(any(Deal.class));
	}

	@Test
	void convertLeadToDeal_WhenLeadNotFound_ShouldThrowException() {
		UUID leadId = UUID.randomUUID();
		BigDecimal amount = BigDecimal.valueOf(5000);

		when(leadRepository.findById(leadId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> dealService.convertLeadToDeal(leadId, amount))
				.isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Lead not found");

		verify(leadRepository).findById(leadId);
		verify(dealRepository, never()).save(any(Deal.class));
	}

	@Test
	void transitionDealStatus_WhenDealExists_ShouldUpdateStatusAndSave() {
		UUID dealId = UUID.randomUUID();
		DealStatus newStatus = DealStatus.QUALIFIED;

		Deal existingDeal = new Deal(UUID.randomUUID(), BigDecimal.TEN);
		existingDeal.setId(dealId);
		when(dealRepository.findById(dealId)).thenReturn(Optional.of(existingDeal));
		when(dealRepository.save(any(Deal.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Deal result = dealService.transitionDealStatus(dealId, newStatus);

		assertThat(result).isNotNull();
		assertThat(result.getStatus()).isEqualTo(newStatus);

		verify(dealRepository).findById(dealId);
		verify(dealRepository).save(existingDeal);
	}

	@Test
	void transitionDealStatus_WhenDealNotFound_ShouldThrowException() {
		UUID dealId = UUID.randomUUID();
		DealStatus newStatus = DealStatus.WON;

		when(dealRepository.findById(dealId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> dealService.transitionDealStatus(dealId, newStatus))
				.isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Deal not found");

		verify(dealRepository).findById(dealId);
		verify(dealRepository, never()).save(any(Deal.class));
	}

	@Test
	void getAllDeals_ShouldReturnAllDeals() {
		List<Deal> deals = Arrays.asList(new Deal(UUID.randomUUID(), BigDecimal.TEN),
				new Deal(UUID.randomUUID(), BigDecimal.valueOf(100)));

		when(dealRepository.findAll()).thenReturn(deals);

		List<Deal> result = dealService.getAllDeals();

		assertThat(result).hasSize(2);
		assertThat(result).containsExactlyInAnyOrderElementsOf(deals);
		verify(dealRepository).findAll();
	}

	@Test
	void getDealsByStatusForKanban_ShouldReturnMapGroupedByStatus() {
		Deal deal1 = new Deal(UUID.randomUUID(), BigDecimal.TEN);
		Deal deal2 = new Deal(UUID.randomUUID(), BigDecimal.valueOf(50));
		deal2.transitionTo(DealStatus.QUALIFIED);

		List<Deal> allDeals = Arrays.asList(deal1, deal2);

		when(dealRepository.findAll()).thenReturn(allDeals);

		Map<DealStatus, List<Deal>> result = dealService.getDealsByStatusForKanban();

		assertThat(result).hasSize(2);
		assertThat(result.get(DealStatus.NEW)).containsExactly(deal1);
		assertThat(result.get(DealStatus.QUALIFIED)).containsExactly(deal2);

		verify(dealRepository).findAll();
	}
}
