package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LeadRepositoryTest {
    private LeadRepository repository;

    @BeforeEach
    void setUp() {
        repository = new LeadRepository();
    }

    @Test
    void shouldSaveAndFindLeadById_whenLeadSaved() {
        UUID id = UUID.randomUUID();
        Contact contact = new Contact("fkaskdla@gmail.com", "89173649173",
                new Address("City", "Street", "123456"));
        Lead lead = new Lead(id, contact, "company F", "NEW");
        repository.save(lead);
        assertThat(repository.findById(id)).isPresent();
    }

    @Test
    void shouldReturnEmpty_whenLeadNotFound() {
        UUID unknownId = UUID.randomUUID();
        assertThat(repository.findById(unknownId)).isEmpty();
    }

    @Test
    void shouldReturnAllLeads_whenMultipleLeadsSaved() {
        Contact contact = new Contact("fkaskdla@gmail.com", "89173649173",
                new Address("City", "Street", "123456"));

        Lead leadOne = new Lead(UUID.randomUUID(), contact, "company F", "NEW");
        Lead leadTwo = new Lead(UUID.randomUUID(), contact, "company F", "NEW");
        Lead leadFree = new Lead(UUID.randomUUID(), contact, "company F", "NEW");

        repository.save(leadOne);
        repository.save(leadTwo);
        repository.save(leadFree);

        assertThat(repository.findAll()).hasSize(3);
    }

    @Test
    void shouldDeleteLead_whenLeadExists() {
        UUID id = UUID.randomUUID();
        Contact contact = new Contact("fkaskdla@gmail.com", "89173649173",
                new Address("City", "Street", "123456"));
        Lead lead = new Lead(id, contact, "company F", "NEW");
        repository.save(lead);
        repository.delete(id);
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void shouldFindFasterWithMap_thanWithListFilter() {
        List<Lead> leadList = new ArrayList<>();
        UUID targetId = null;

        for (int i = 0; i < 1000; i++) {
            UUID id = UUID.randomUUID();
            Contact contact = new Contact(
                    "email" + i + "@test.com",
                    "+7" + String.format("%010d", i),
                    new Address("City" + i, "Street" + i, "ZIP" + i)
            );
            Lead lead = new Lead(id, contact, "Company" + i, "NEW");
            repository.save(lead);
            leadList.add(lead);

            if (i == 500) {
                targetId = id;
            }
        }

        long mapStart = System.nanoTime();
        Lead foundInMap = repository.findById(targetId).orElse(null);
        long mapDuration = System.nanoTime() - mapStart;

        long listStart = System.nanoTime();
        UUID finalTargetId = targetId;
        Lead foundInList = leadList.stream()
                .filter(lead -> lead.id().equals(finalTargetId))
                .findFirst()
                .orElse(null);
        long listDuration = System.nanoTime() - listStart;

        assertThat(foundInMap).isEqualTo(foundInList);
        assertThat(listDuration).isGreaterThan(mapDuration * 10);

        System.out.println("Map поиск: " + mapDuration + " ns");
        System.out.println("List поиск: " + listDuration + " ns");
        System.out.println("Ускорение: " + (listDuration / mapDuration) + "x");
    }
    @Test
    void shouldSaveBothLeads_evenWithSameEmailAndPhone_becauseRepositoryDoesNotCheckBusinessRules() {
        Contact sharedContact = new Contact("ivan@mail.ru", "+79001234567",
                new Address("Moscow", "Tverskaya 1", "101000"));
        Lead originalLead = new Lead(UUID.randomUUID(), sharedContact, "Acme Corp", "NEW");
        Lead duplicateLead = new Lead(UUID.randomUUID(), sharedContact, "TechCorp", "QUALIFIED"); // ← ИСПРАВЛЕНО!

        repository.save(originalLead);
        repository.save(duplicateLead);

        assertThat(repository.size()).isEqualTo(2);
    }
}