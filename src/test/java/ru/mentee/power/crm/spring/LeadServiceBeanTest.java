package ru.mentee.power.crm.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.spring.repository.LeadRepository;
import ru.mentee.power.crm.spring.service.LeadService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class LeadServiceBeanTest {
  @Autowired
  private ApplicationContext context;

  @Test
  void shouldCreateLeadServiceBean() {
    LeadService service = context.getBean(LeadService.class);
    assertThat(service).isNotNull();
  }

  @Test
  void shouldCreateLeadRepositoryBean() {
    LeadRepository repo = context.getBean(LeadRepository.class);
    assertThat(repo).isNotNull();
  }

  @Test
  void shouldInjectLeadRepositoryIntoService() {
    LeadService service = context.getBean(LeadService.class);
    assertThat(service.findAll()).isNotNull();
  }
}
