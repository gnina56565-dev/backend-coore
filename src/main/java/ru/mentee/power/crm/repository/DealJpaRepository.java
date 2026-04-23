package ru.mentee.power.crm.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mentee.power.crm.domain.Deal;

import java.util.Optional;
import java.util.UUID;

public interface DealJpaRepository extends JpaRepository<Deal, UUID> {

  @EntityGraph(attributePaths = {"dealProducts", "dealProducts.product"}, type = EntityGraph.EntityGraphType.FETCH)
  @Query("SELECT d FROM Deal d WHERE d.id = :id")
  Optional<Deal> findDealWithProducts(UUID id);
}
