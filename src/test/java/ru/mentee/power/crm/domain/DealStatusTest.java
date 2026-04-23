package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DealStatusTest {

	@ParameterizedTest
	@CsvSource({"NEW, QUALIFIED, true", "NEW, LOST, true", "NEW, WON, false", "QUALIFIED, PROPOSAL_SENT, true",
			"PROPOSAL_SENT, NEGOTIATION, true", "NEGOTIATION, WON, true", "NEGOTIATION, LOST, true", "WON, NEW, false",
			"LOST, QUALIFIED, false"})
	void shouldValidateTransitions(DealStatus from, DealStatus to, boolean expected) {
		assertThat(from.canTransitionTo(to)).isEqualTo(expected);
	}

	@Test
	void terminalStates_shouldNotAllowAnyTransitions() {
		Deal wonDeal = new Deal(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, DealStatus.WON,
				LocalDateTime.now());
		wonDeal.getStatus().canTransitionTo(DealStatus.NEW);
		for (DealStatus targetStatus : DealStatus.values()) {
			assertFalse(wonDeal.getStatus().canTransitionTo(targetStatus));
		}
		Deal lostDeal = new Deal(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, DealStatus.LOST,
				LocalDateTime.now());
		for (DealStatus targetStatus : DealStatus.values()) {
			assertFalse(lostDeal.getStatus().canTransitionTo(targetStatus));
		}
	}
}
