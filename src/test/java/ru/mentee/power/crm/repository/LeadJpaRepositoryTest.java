package ru.mentee.power.crm.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@ActiveProfiles("test")
class LeadJpaRepositoryTest {

    @Autowired
    private LeadJpaRepository repository;

    private Lead lead1;
    private Lead lead2;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        lead1 = new Lead("test1@example.com", "Company1", LeadStatus.NEW);
        lead1.setEmail("john@example.com");
        lead1.setCompany("ACME Corp");
        lead1.setStatus(LeadStatus.NEW);
        lead1.setCreatedAt(LocalDateTime.now().minusDays(5));
        repository.save(lead1);

        lead2 = new Lead("test2@example.com", "Company2", LeadStatus.NEW);
        lead2.setEmail("jane@example.com");
        lead2.setCompany("Tech Inc");
        lead2.setStatus(LeadStatus.CONTACTED);
        lead2.setCreatedAt(LocalDateTime.now().minusDays(2));
        repository.save(lead2);
    }

    @Test
    void findByEmail_shouldReturnLead_whenExists() {
        Optional<Lead> found = repository.findByEmail("john@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getCompany()).isEqualTo("ACME Corp");
    }

    @Test
    void findByStatus_shouldReturnFilteredLeads() {
        List<Lead> newLeads = repository.findByStatus(LeadStatus.NEW);

        assertThat(newLeads).hasSize(1);
        assertThat(newLeads.get(0).getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void findByStatusIn_shouldReturnLeadsWithMultipleStatuses() {
        List<LeadStatus> statuses = List.of(LeadStatus.NEW, LeadStatus.CONTACTED);

        List<Lead> found = repository.findByStatusIn(statuses);

        assertThat(found).hasSize(2);
    }

    @Test
    void findAll_withPageable_shouldReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 1);

        Page<Lead> page = repository.findAll(pageRequest);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    void shouldCountByStatus_Valid() {
        long countNew = repository.countByStatus(LeadStatus.NEW);
        long countContacted = repository.countByStatus(LeadStatus.CONTACTED);

        assertThat(countNew).isEqualTo(1);
        assertThat(countContacted).isEqualTo(1);
    }

    @Test
    void shouldExistsByEmail_WhenEmailNotExists() {
        boolean exists = repository.existsByEmail("john@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    void shouldFindByStatusAndCompany_Valid() {
        List<Lead> found = repository.findByStatusAndCompany(LeadStatus.NEW, "ACME Corp");

        assertThat(found.get(0).getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldUpdateStatusBulk_Valid() {
        int updatedCount = repository.updateStatusBulk(LeadStatus.NEW, LeadStatus.CONTACTED);
        repository.flush();
        assertThat(updatedCount).isEqualTo(1);

        Optional<Lead> updatedLead = repository.findById(lead1.getId());
        assertThat(updatedLead).isPresent();
        assertThat(updatedLead.get().getStatus()).isEqualTo(LeadStatus.CONTACTED);
    }
}
