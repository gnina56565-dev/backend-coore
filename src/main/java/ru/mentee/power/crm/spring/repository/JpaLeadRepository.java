package ru.mentee.power.crm.spring.repository;

import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.repository.LeadJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaLeadRepository implements LeadRepository {

  private final LeadJpaRepository leadJpaRepository;

  public JpaLeadRepository(LeadJpaRepository leadJpaRepository) {
    this.leadJpaRepository = leadJpaRepository;
  }

  @Override
  public Lead save(Lead lead) {
    return leadJpaRepository.save(lead);
  }

  @Override
  public Optional<Lead> findById(UUID id) {
    return leadJpaRepository.findById(id);
  }

  @Override
  public Optional<Lead> findByEmail(String email) {
    return leadJpaRepository.findByEmail(email);
  }

  @Override
  public List<Lead> findAll() {
    return leadJpaRepository.findAll();
  }

  @Override
  public void delete(UUID id) {
    leadJpaRepository.deleteById(id);
  }
}
