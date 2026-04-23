package ru.mentee.power.crm.spring.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = NONE)
@ComponentScan(basePackages = "ru.mentee.power.crm.spring.repository", includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
class JpaLeadRepositoryTest {

  @Autowired
  private JpaLeadRepository repository;

  @Autowired
  private LeadJpaRepository leadJpaRepository;

  @Autowired
  private CompanyRepository companyRepository;

  @Autowired
  private TestEntityManager entityManager;

  private Company testCompany;
  private Lead savedLead;

  @BeforeEach
  void setUp() {
    testCompany = companyRepository.save(new Company("Test Corp", "IT"));
    Lead lead = new Lead("test@example.com", testCompany, LeadStatus.NEW);
    savedLead = repository.save(lead);
  }

  @AfterEach
  void tearDown() {
    companyRepository.deleteAll();
  }

  @Test
  void shouldSaveLead() {
    Company newCompany = companyRepository.save(new Company("New Corp", "Sales"));
    Lead newLead = new Lead("new@example.com", newCompany, LeadStatus.CONTACTED);

    Lead result = repository.save(newLead);

    assertThat(result.getId()).isNotNull();
    assertThat(result.getEmail()).isEqualTo("new@example.com");

    Optional<Lead> found = leadJpaRepository.findById(result.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getStatus()).isEqualTo(LeadStatus.CONTACTED);
  }

  @Test
  void shouldFindById() {
    Optional<Lead> found = repository.findById(savedLead.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    assertThat(found.get().getCompany().getName()).isEqualTo("Test Corp");
  }

  @Test
  void shouldReturnEmptyWhenIdNotFound() {
    UUID nonExistentId = UUID.randomUUID();
    Optional<Lead> found = repository.findById(nonExistentId);

    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindByEmail() {
    Optional<Lead> found = repository.findByEmail("test@example.com");

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(savedLead.getId());
  }

  @Test
  void shouldReturnEmptyWhenEmailNotFound() {
    Optional<Lead> found = repository.findByEmail("nonexistent@example.com");
    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindAll() {
    Lead lead2 = new Lead("second@example.com", testCompany, LeadStatus.NEW);
    repository.save(lead2);

    List<Lead> all = repository.findAll();

    assertThat(all).hasSize(2);
    assertThat(all).extracting("email").containsExactlyInAnyOrder("test@example.com", "second@example.com");
  }

  @Test
  void shouldDeleteById() {
    UUID idToDelete = savedLead.getId();

    assertThat(repository.findById(idToDelete)).isPresent();

    repository.delete(idToDelete);

    assertThat(repository.findById(idToDelete)).isEmpty();
    assertThat(leadJpaRepository.findById(idToDelete)).isEmpty();
  }

  @Test
  void shouldNotFailWhenDeletingNonExistentId() {
    UUID nonExistentId = UUID.randomUUID();
    repository.delete(nonExistentId);
    assertThat(true).isTrue();
  }
}
