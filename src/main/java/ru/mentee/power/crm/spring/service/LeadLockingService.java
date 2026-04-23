package ru.mentee.power.crm.spring.service;

import jakarta.persistence.OptimisticLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadJpaRepository;

import java.util.List;
import java.util.UUID;

@Service
public class LeadLockingService {

	private final LeadJpaRepository leadRepository;

	public LeadLockingService(LeadJpaRepository leadRepository) {
		this.leadRepository = leadRepository;
	}

	@Transactional
	public Lead convertLeadToDealWithLock(UUID leadId, LeadStatus newStatus) {

		Lead lead = leadRepository.findByIdForUpdate(leadId)
				.orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

		lead.setStatus(newStatus);
		return leadRepository.save(lead);
	}

	@Transactional
	public Lead updateLeadStatusOptimistic(UUID leadId, LeadStatus newStatus) {
		Lead lead = leadRepository.findById(leadId)
				.orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

		lead.setStatus(newStatus);
		return leadRepository.save(lead);
	}

	@Transactional
	public Lead updateWithRetry(UUID leadId, LeadStatus newStatus) {
		try {
			return updateLeadStatusOptimistic(leadId, newStatus);
		} catch (OptimisticLockException e) {
			throw new RuntimeException("attempts due to optimistic lock conflicts", e);
		}
	}
	@Transactional
	public void processLeadsInOrder(List<UUID> ids) {
		for (UUID id : ids) {
			Lead lead = leadRepository.findByIdForUpdate(id)
					.orElseThrow(() -> new IllegalArgumentException("Lead not found: " + id));
			lead.setStatus(LeadStatus.CONTACTED);
			leadRepository.save(lead);
		}
	}
}
