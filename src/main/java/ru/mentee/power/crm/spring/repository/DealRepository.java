package ru.mentee.power.crm.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DealRepository extends JpaRepository<Deal, UUID> {

  Deal save(Deal deal);
  Optional<Deal> findById(UUID id);
  List<Deal> findAll();
  List<Deal> findByStatus(DealStatus status);
  void deleteById(UUID id);
}
