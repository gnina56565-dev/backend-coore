// ru.mentee.power.crm.repo.InMemoryLeadRepositoryTest
package ru.mentee.power.crm.repo;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryLeadRepositoryTest {

    @Test
    void shouldAddLeadAndFindIt() {
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        UUID id = UUID.randomUUID();
        Address address = new Address("Samara", "Street V", "9374");
        Contact contact = new Contact("test@gmail.com", "+937481294738", address);
        Lead firstLead = new Lead(id, contact, "company Z", "NEW");
        repository.add(firstLead);
        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findById(id)).contains(firstLead);
    }
    @Test
    void shouldReturnEmptyForNonExistentId() {
        InMemoryLeadRepository repo = new InMemoryLeadRepository();
        Address address = new Address("Samara", "Street V", "9374");
        Contact contact = new Contact("test@gmail.com", "+937481294738", address);
        for (int i = 1; i <= 10; i++) {
            repo.add(new Lead(UUID.randomUUID(), contact, "company Z", "NEW"));
        }
        UUID newId = UUID.randomUUID();
        assertThat(repo.findById(newId)).isEmpty();
    }
    @Test
    void shouldAddEmailAndAddSecondLead() {
        InMemoryLeadRepository repo = new InMemoryLeadRepository();
        UUID id = UUID.randomUUID();
        Address address = new Address("Samara", "Street V", "9374");
        Contact contact = new Contact("test@gmail.com", "+937481294738", address);
        Lead firstLead = new Lead(id, contact, "company Z", "NEW");
        Lead SecondLead = new Lead(id, contact, "company Z", "NEW");
        repo.add(firstLead);
        repo.add(SecondLead);
        assertThat(repo.findAll()).hasSize(1);
    }
    @Test
    void shouldRemoveLead() {
        InMemoryLeadRepository repo = new InMemoryLeadRepository();
        Address address = new Address("Samara", "Street V", "9374");
        Contact contact = new Contact("test@gmail.com", "+937481294738", address);
        UUID idForRemove = null;
        for (int i = 1; i <= 5; i++) {
            UUID id = UUID.randomUUID();
            if (i == 1) {
                idForRemove = id;
            }
            repo.add(new Lead(id, contact, "company Z", "NEW"));
        }
        repo.remove(idForRemove);
        assertThat(repo.findAll()).hasSize(4);
        assertThat(repo.findById(idForRemove)).isEmpty();
    }
    @Test
    void shouldRefactorList() {
        InMemoryLeadRepository repo = new InMemoryLeadRepository();
        UUID id = UUID.randomUUID();
        Address address = new Address("Samara", "Street V", "9374");
        Contact contact = new Contact("test@gmail.com", "+937481294738", address);
        Lead firstLead = new Lead(id, contact, "company Z", "NEW");
        repo.add(firstLead);
        List<Lead> newList = repo.findAll();
        newList.clear();
        assertThat(repo.findAll()).hasSize(1);

    }
}