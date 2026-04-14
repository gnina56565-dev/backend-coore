package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.model.Company;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryLeadJpaRepositoryTest {

    private InMemoryLeadRepository repository;
    private Lead lead1;
    private Lead lead2;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLeadRepository();
        Company company1 = new Company("test1", "General");
        Company company2 = new Company("test2", "General");
        lead1 = new Lead(UUID.randomUUID(), "test1@example.com", company1, LeadStatus.NEW);
        lead2 = new Lead(UUID.randomUUID(), "test2@example.com", company2, LeadStatus.CONTACTED);
    }

    @Test
    void shouldSaveLead() {
        Lead savedLead = repository.save(lead1);
        assertThat(savedLead).isEqualTo(lead1);
        assertThat(repository.findById(lead1.getId())).contains(lead1);
        assertThat(repository.findByEmail(lead1.getEmail())).contains(lead1);
    }

    @Test
    void shouldFindLeadById() {
        repository.save(lead1);
        Optional<Lead> foundLead = repository.findById(lead1.getId());
        assertThat(foundLead).contains(lead1);
    }

    @Test
    void shouldReturnEmptyWhenLeadNotFoundById() {
        Optional<Lead> foundLead = repository.findById(UUID.randomUUID());
        assertThat(foundLead).isEmpty();
    }

    @Test
    void shouldFindLeadByEmail() {
        repository.save(lead1);
        Optional<Lead> foundLead = repository.findByEmail(lead1.getEmail());
        assertThat(foundLead).contains(lead1);
    }

    @Test
    void shouldReturnEmptyWhenLeadNotFoundByEmail() {
        Optional<Lead> foundLead = repository.findByEmail("nonexistent@example.com");
        assertThat(foundLead).isEmpty();
    }

    @Test
    void shouldFindAllLeads() {
        repository.save(lead1);
        repository.save(lead2);
        List<Lead> leads = repository.findAll();
        assertThat(leads).hasSize(2);
        assertThat(leads).contains(lead1, lead2);
    }

    @Test
    void shouldDeleteLead() {
        repository.save(lead1);
        repository.delete(lead1);
        assertThat(repository.findById(lead1.getId())).isEmpty();
        assertThat(repository.findByEmail(lead1.getEmail())).isEmpty();
    }

    @Test
    void shouldNotFailWhenDeletingNonExistentLead() {
        repository.delete(lead1);
        assertThat(repository.findAll()).isEmpty();
    }
}