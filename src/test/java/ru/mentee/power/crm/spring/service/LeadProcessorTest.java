package ru.mentee.power.crm.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadJpaRepository;
import ru.mentee.power.crm.spring.repository.DealRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeadProcessorTest {

    @Mock
    private LeadJpaRepository leadJpaRepository;

    @Mock
    private DealRepository dealRepository;

    private LeadProcessor leadProcessor;

    @BeforeEach
    void setUp() {
        leadProcessor = new LeadProcessor(leadJpaRepository, dealRepository);
    }

    @Test
    void processSingleLead_WhenLeadExists_ShouldUpdateStatusToContacted() {
        UUID leadId = UUID.randomUUID();
        Lead lead = new Lead("test@example.com", new Company("Test", null), LeadStatus.NEW);

        when(leadJpaRepository.findById(leadId)).thenReturn(Optional.of(lead));
        when(leadJpaRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leadProcessor.processSingleLead(leadId);

        assertThat(lead.getStatus()).isEqualTo(LeadStatus.CONTACTED);
        verify(leadJpaRepository).findById(leadId);
        verify(leadJpaRepository).save(lead);
    }

    @Test
    void processSingleLead_WhenLeadNotFound_ShouldThrowException() {
        UUID leadId = UUID.randomUUID();

        when(leadJpaRepository.findById(leadId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leadProcessor.processSingleLead(leadId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Lead not found");

        verify(leadJpaRepository).findById(leadId);
        verify(leadJpaRepository, never()).save(any(Lead.class));
    }

    @Test
    void processSingleLead_WhenEmailContainsTrigger_ShouldThrowRuntimeException() {
        UUID leadId = UUID.randomUUID();
        Lead lead = new Lead("throw-exception@example.com", new Company("Test", null), LeadStatus.NEW);

        when(leadJpaRepository.findById(leadId)).thenReturn(Optional.of(lead));

        assertThatThrownBy(() -> leadProcessor.processSingleLead(leadId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Test failure for lead");

        verify(leadJpaRepository).findById(leadId);
        verify(leadJpaRepository, never()).save(any(Lead.class));
    }

    @Test
    void methodRequired_ShouldExecuteWithoutError() {
        when(leadJpaRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leadProcessor.methodRequired();

        verify(leadJpaRepository).save(any(Lead.class));
    }

    @Test
    void methodRequiredNew_ShouldExecuteWithoutError() {
        when(leadJpaRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leadProcessor.methodRequiredNew();

        verify(leadJpaRepository).save(any(Lead.class));
    }

    @Test
    void methodReadCommitted_ShouldExecuteWithoutError() {
        when(leadJpaRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leadProcessor.methodReadCommitted();

        verify(leadJpaRepository).save(any(Lead.class));
    }

    @Test
    void methodMandatory_ShouldExecuteWithoutError() {
        when(leadJpaRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leadProcessor.methodMandatory();

        verify(leadJpaRepository).save(any(Lead.class));
    }

    @Test
    void methodRepeatableRead_ShouldExecuteWithoutError() {
        when(leadJpaRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leadProcessor.methodRepeatableRead();

        verify(leadJpaRepository).save(any(Lead.class));
    }
}