package ru.mentee.power.crm.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LeadRepositoryTest {

    @Test
    @DisplayName("Should automatically deduplicate leads by id")
    void shouldDeduplicateLeadsById() {
        LeadRepository repo = new LeadRepository();
        UUID id = UUID.randomUUID();
        Address address = new Address("Samara", "Street V", "9374");
        Contact contact = new Contact("test@gmail.com", "+937481294738", address);
        Lead lead = new Lead(id, contact, "company Z", "NEW");
        repo.add(lead);
        repo.add(lead);
        assertThat(repo.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should allow different leads with different ids")
    void shouldAllowDifferentLeads() {
        LeadRepository repo = new LeadRepository();
        Address address = new Address("Samara", "Street V", "9374");
        Contact contact = new Contact("test@gmail.com", "+937481294738", address);
        Lead firstlead = new Lead(UUID.randomUUID(), contact, "company Z", "NEW");
        Lead secondlead = new Lead(UUID.randomUUID(), contact, "company Z", "NEW");
        repo.add(firstlead);
        repo.add(secondlead);
        assertThat(repo.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should find existing lead through contains")
    void shouldFindExistingLead() {
        LeadRepository repo = new LeadRepository();
        UUID id = UUID.randomUUID();
        Address address = new Address("Samara", "Street V", "9374");
        Contact contact = new Contact("test@gmail.com", "+937481294738", address);
        Lead lead = new Lead(id, contact, "company Z", "NEW");
        repo.add(lead);
        assertThat(repo.contains(lead)).isTrue();
    }

    @Test
    @DisplayName("Should return unmodifiable set from findAll")
    void shouldReturnUnmodifiableSet() {
        LeadRepository repo = new LeadRepository();
        UUID id = UUID.randomUUID();
        Address address = new Address("Samara", "Street V", "9374");
        Contact contact = new Contact("test@gmail.com", "+937481294738", address);
        Lead lead = new Lead(id, contact, "company Z", "NEW");
        repo.add(lead);
        Set<Lead> res = repo.findAll();
        assertThatThrownBy(() -> res.add(lead))
                .isInstanceOf(UnsupportedOperationException.class);
    }
    @Test
    @DisplayName("Should perform contains() faster than ArrayList")
    void shouldPerformFasterThanArrayList() {
        LeadRepository repo = new LeadRepository();
        UUID id = UUID.randomUUID();
        Address address = new Address("Samara", "Street V", "9374");
        Contact contact = new Contact("test@gmail.com", "+937481294738", address);
        Lead lead = new Lead(id, contact, "company Z", "NEW");
        List<Lead> arrayList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            arrayList.add(lead);
        }
        Set<Lead> hashSet = new HashSet<>(arrayList);
        Lead newlead = new Lead(UUID.randomUUID(), contact, "company Z", "NEW");
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            arrayList.contains(newlead);
        }
        long arrayListDuration = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            hashSet.contains(newlead);
        }
        long hashSetDuration = System.nanoTime() - start;
        assertThat(arrayListDuration / hashSetDuration).isGreaterThan(100);
    }
}