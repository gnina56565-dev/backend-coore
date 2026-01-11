//package ru.mentee.power.crm.storage;
//
//import org.junit.jupiter.api.Test;
//import ru.mentee.power.crm.domain.Lead;
//import java.util.UUID;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//
//class LeadStorageTest {
//
//    @Test
//    void shouldAddLead_whenLeadIsUnique() {
//        LeadStorage storage = new LeadStorage();
//        Lead uniqueLead = new Lead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//        boolean added = storage.add(uniqueLead);
//
//        assertThat(added).isTrue();
//        assertThat(storage.size()).isEqualTo(1);
//        assertThat(storage.findAll()).containsExactly(uniqueLead);
//    }
//
//    @Test
//    void shouldRejectDuplicate_whenEmailAlreadyExists() {
//        LeadStorage storage = new LeadStorage();
//        Lead existingLead = new Lead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//        Lead duplicateLead = new Lead(UUID.randomUUID(), "ivan@mail.ru", "+7456", "Other", "NEW");
//        storage.add(existingLead);
//
//        boolean added = storage.add(duplicateLead);
//
//        assertThat(added).isFalse();
//        assertThat(storage.size()).isEqualTo(1);
//        assertThat(storage.findAll()).containsExactly(existingLead);
//    }
//
//    @Test
//    void shouldThrowException_whenStorageIsFull() {
//        LeadStorage storage = new LeadStorage();
//        for (int i = 0; i < 100; i++) {
//            storage.add(new Lead(UUID.randomUUID(), "lead" + i + "@mail.ru", "+7000", "Company", "NEW"));
//        }
//
//        Lead hundredFirstLead = new Lead(UUID.randomUUID(), "lead101@mail.ru", "+7001", "Company", "NEW");
//
//        assertThatThrownBy(() -> storage.add(hundredFirstLead))
//                .isInstanceOf(IllegalStateException.class)
//                .hasMessageContaining("Storage is full");
//    }
//
//    @Test
//    void shouldReturnOnlyAddedLeads_whenFindAllCalled() {
//        LeadStorage storage = new LeadStorage();
//        Lead firstLead = new Lead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");
//        Lead secondLead = new Lead(UUID.randomUUID(), "maria@startup.io", "+7456", "StartupLab", "NEW");
//        storage.add(firstLead);
//        storage.add(secondLead);
//
//        Lead[] result = storage.findAll();
//
//        assertThat(result).hasSize(2);
//        assertThat(result).containsExactly(firstLead, secondLead);
//    }
//}