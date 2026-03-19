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

    // Критическая операция с pessimistic lock
    @Transactional
    public Lead convertLeadToDealWithLock(UUID leadId, LeadStatus newStatus) {
        // Блокируем Lead эксклюзивно до конца транзакции
        Lead lead = leadRepository.findByIdForUpdate(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

        // Здесь могла бы быть сложная бизнес-логика конверсии:
        // - создание Deal
        // - обновление статуса Lead
        // - отправка уведомлений
        // Другие транзакции ЖДУТ завершения этой операции

        lead.setStatus(newStatus);
        return leadRepository.save(lead);
    }

    // Обычное обновление с optimistic lock (через @Version)
    @Transactional
    public Lead updateLeadStatusOptimistic(UUID leadId, LeadStatus newStatus) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

        // Блокировки НЕТ — другие транзакции могут читать и изменять
        // При сохранении JPA проверит version и выбросит OptimisticLockException если конфликт

        lead.setStatus(newStatus);
        return leadRepository.save(lead);
        // UPDATE leads SET status=?, version=version+1 WHERE id=? AND version=?
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