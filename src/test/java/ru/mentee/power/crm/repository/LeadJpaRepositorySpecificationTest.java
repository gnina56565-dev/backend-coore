package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.specification.LeadSpecifications;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class LeadJpaRepositorySpecificationTest {

  @Autowired
  private LeadJpaRepository leadRepository;

  @Autowired
  private TestEntityManager entityManager;

  private Company companyA;
  private Company companyB;

  @BeforeEach
  void setUp() {
    companyA = new Company("Company A", "IT");
    companyB = new Company("Company B", "Sales");
    entityManager.persist(companyA);
    entityManager.persist(companyB);

    Lead lead1 = new Lead("john@example.com", companyA, LeadStatus.NEW);
    Lead lead2 = new Lead("jane@example.com", companyA, LeadStatus.CONTACTED);
    Lead lead3 = new Lead("bob@test.com", companyB, LeadStatus.NEW);
    Lead lead4 = new Lead("alice@example.com", companyB, LeadStatus.QUALIFIED);

    entityManager.persist(lead1);
    entityManager.persist(lead2);
    entityManager.persist(lead3);
    entityManager.persist(lead4);

    entityManager.flush();
    entityManager.clear();
  }

  @Test
  void shouldFilterByEmailOnly() {
    var spec = LeadSpecifications.buildFilter("example", null);
    List<Lead> result = leadRepository.findAll(spec);

    assertThat(result).hasSize(3);
    assertThat(result).extracting("email").containsExactlyInAnyOrder("john@example.com", "jane@example.com",
        "alice@example.com");
  }

  @Test
  void shouldFilterByStatusOnly() {
    var spec = LeadSpecifications.buildFilter(null, LeadStatus.NEW);
    List<Lead> result = leadRepository.findAll(spec);

    assertThat(result).hasSize(2);
    assertThat(result).extracting("email").containsExactlyInAnyOrder("john@example.com", "bob@test.com");
  }

  @Test
  void shouldFilterByBothEmailAndStatus() {
    var spec = LeadSpecifications.buildFilter("example", LeadStatus.NEW);
    List<Lead> result = leadRepository.findAll(spec);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getEmail()).isEqualTo("john@example.com");
  }

  @Test
  void shouldReturnAllWhenFiltersAreNull() {
    var spec = LeadSpecifications.buildFilter(null, null);
    List<Lead> result = leadRepository.findAll(spec);

    assertThat(result).hasSize(4);
  }

  @Test
  void shouldHandleCaseInsensitiveEmail() {
    var spec = LeadSpecifications.buildFilter("JOHN@", null);
    List<Lead> result = leadRepository.findAll(spec);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getEmail()).isEqualTo("john@example.com");
  }
}
