package ru.mentee.power.crm.spring.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.spring.repository.DealRepository;
import ru.mentee.power.crm.spring.repository.LeadRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DealService {
  private final DealRepository dealRepository;
  private final LeadRepository leadRepository;

  public DealService(DealRepository dealRepository, LeadRepository leadRepository) {
    this.dealRepository = dealRepository;
    this.leadRepository = leadRepository;
  }

  @Transactional
  public Deal convertLeadToDeal(UUID leadId, BigDecimal amount) {
    leadRepository.findById(leadId).orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));
    boolean hasDeal = dealRepository.findAll().stream()
        .anyMatch(d -> d.getLeadId() != null && d.getLeadId().equals(leadId));

    if (hasDeal) {
      throw new IllegalStateException("Сделка для этого лида уже существует!");
    }

    Deal newDeal = new Deal(leadId, amount);
    if (newDeal.getStatus() == null) {
      newDeal.setStatus(DealStatus.NEW);
    }
    return dealRepository.save(newDeal);
  }

  public Deal transitionDealStatus(UUID dealId, DealStatus newStatus) {
    Deal deal = dealRepository.findById(dealId)
        .orElseThrow(() -> new IllegalArgumentException("Deal not found: " + dealId));
    deal.transitionTo(newStatus);
    return dealRepository.save(deal);
  }

  public List<Deal> getAllDeals() {
    return dealRepository.findAll();
  }

  public Map<DealStatus, List<Deal>> getDealsByStatusForKanban() {
    return dealRepository.findAll().stream().collect(Collectors.groupingBy(Deal::getStatus));
  }
}
