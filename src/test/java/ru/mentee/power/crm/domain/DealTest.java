package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static ru.mentee.power.crm.domain.DealStatus.NEW;

class DealTest {

	@Test
	void shouldCreateDeal_withNewStatus() {
		UUID leadId = UUID.randomUUID();
		BigDecimal amount = new BigDecimal("100000.00");

		Deal deal = new Deal(leadId, amount);
		assertThat(deal).isNotNull();
		assertThat(deal.getLeadId()).isEqualTo(leadId);
		assertThat(deal.getAmount()).isEqualTo(amount);
		assertThat(deal.getStatus()).isEqualTo(NEW);
		assertThat(deal.getCreatedAt()).isNotNull();
	}

	@Test
	void shouldTransitionToValidStatus() {
		UUID leadId = UUID.randomUUID();
		BigDecimal amount = new BigDecimal("100000.00");
		Deal deal = new Deal(leadId, amount);
		assertThat(deal.getStatus()).isEqualTo(NEW);
		deal.transitionTo(DealStatus.QUALIFIED);
		assertThat(deal.getStatus()).isEqualTo(DealStatus.QUALIFIED);
	}

	@Test
	void shouldThrowException_whenTransitionInvalid() {
		Deal deal = new Deal(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("10000.0"), DealStatus.WON,
				LocalDateTime.now());

		assertThatThrownBy(() -> deal.transitionTo(DealStatus.NEW)).isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("Cannot transition").hasMessageContaining("WON").hasMessageContaining("NEW");
	}
}
