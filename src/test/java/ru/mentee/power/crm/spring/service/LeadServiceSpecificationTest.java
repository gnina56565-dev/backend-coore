package ru.mentee.power.crm.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadJpaRepository;
import ru.mentee.power.crm.spring.repository.CompanyRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LeadServiceSpecificationTest {

  @Autowired
  private LeadService leadService;

  @Autowired
  private LeadJpaRepository leadRepository;

  @Autowired
  private CompanyRepository companyRepository;

  private Company testCompany;

  @BeforeEach
  void setUp() {
    testCompany = companyRepository.save(new Company("Test Corp", "IT"));

    leadRepository.save(new Lead("user1@test.com", testCompany, LeadStatus.NEW));
    leadRepository.save(new Lead("user2@test.com", testCompany, LeadStatus.CONTACTED));
    leadRepository.save(new Lead("admin@test.com", testCompany, LeadStatus.NEW));
  }

  @Test
  void findLeadsBySpec_ShouldFilterCorrectly() {
    List<Lead> result = leadService.findLeadsBySpec("user", "NEW");
    assertThat(result).extracting("email").contains("user1@test.com");
    assertThat(result).extracting("email").doesNotContain("user2@test.com", "admin@test.com");
  }

  @Test
  void findLeadsBySpec_ShouldReturnAllIfFiltersEmpty() {
    List<Lead> result = leadService.findLeadsBySpec(null, null);
    assertThat(result).extracting("email").containsAll(List.of("user1@test.com", "user2@test.com", "admin@test.com"));
    assertThat(result).hasSizeGreaterThanOrEqualTo(3);
  }

  @Test
  void findLeadsBySpec_ShouldHandleInvalidStatusGracefully() {
    List<Lead> result = leadService.findLeadsBySpec("admin", "INVALID_STATUS");
    assertThat(result).extracting("email").contains("admin@test.com");
  }
}
