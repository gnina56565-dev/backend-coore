package ru.mentee.power.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadJpaRepository;
import ru.mentee.power.crm.spring.service.LeadService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class LeadServiceTest {

    @Autowired
    private LeadService service;

    @Autowired
    private LeadJpaRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        for (int i = 1; i <= 3; i++) {
            Lead lead = new Lead("lead" + i + "@example.com", "Company " + i, LeadStatus.NEW);
            lead.setEmail("lead" + i + "@example.com");
            lead.setCompany("Company " + i);
            lead.setStatus(LeadStatus.NEW);
            repository.save(lead);
        }
    }

    @Test
    void convertNewToContacted_shouldUpdateMultipleLeads() {
        int updated = service.convertNewToContacted();

        assertThat(updated).isEqualTo(3);

        long contactedCount = repository.countByStatus(LeadStatus.CONTACTED);
        assertThat(contactedCount).isEqualTo(3);

        long newCount = repository.countByStatus(LeadStatus.NEW);
        assertThat(newCount).isEqualTo(0);
    }

    @Test
    void archiveOldLeads_Bulk() {
        int archive = service.archiveOldLeads(LeadStatus.NEW);

        assertThat(archive).isEqualTo(3);
        assertThat(repository.countByStatus(LeadStatus.NEW)).isEqualTo(0);
    }

}