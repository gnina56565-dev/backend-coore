package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LeadRepositoryTest {

    @Autowired
    private LeadRepository repository;

    @Test
    void shouldSaveAndFindLeadById_whenValidData() {
        Lead lead = new Lead(UUID.randomUUID(), "test@example.com", "ACME", LeadStatus.NEW);
        Lead saved = repository.save(lead);
        Optional<Lead> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldFindByEmailNative_whenLeadExists() {
        Lead lead = new Lead(UUID.randomUUID(), "native@test.com", "TechCorp", LeadStatus.NEW);
        repository.save(lead);
        Optional<Lead> found = repository.findByEmailNative("native@test.com");
        assertThat(found).isPresent();
        assertThat(found.get().getCompany()).isEqualTo("TechCorp");
    }

    @Test
    void shouldReturnEmptyOptional_whenEmailNotFound() {
        Optional<Lead> found = repository.findByEmailNative("nonexistent@test.com");
        assertThat(found).isEmpty();
    }

    @Test
    void shoulFindAll_whenLeadsExists(){
        UUID id = UUID.randomUUID();
        Lead lead = new Lead(id, "exists@email.com", "TechCorp", LeadStatus.NEW);
        repository.save(lead);
        List<Lead> found = repository.findAll();

        assertThat(found.get(0).getEmail()).isEqualTo("exists@email.com");
    }
    @Test
    void shouldDelete_whenLeadsExists() {
        UUID id = UUID.randomUUID();
        Lead lead = new Lead(id, "exists@email.com", "TechCorp", LeadStatus.NEW);
        Lead saved = repository.save(lead);
        repository.deleteById(saved.getId());

        assertThat(repository.findById(saved.getId())).isEmpty();
    }
}