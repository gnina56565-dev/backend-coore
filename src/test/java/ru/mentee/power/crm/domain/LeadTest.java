package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class LeadTest {

    @Test
    void shouldCreateLead_whenValidData() {
        UUID uuid = UUID.randomUUID();
        Lead lead = new Lead(uuid, "test@example.com", "+71234567890", "TestCorp", "NEW");
        assertThat(lead.id()).isEqualTo(uuid);
    }

    @Test
    void shouldGenerateUniqueIds_whenMultipleLeads() {
        Lead lead1 = new Lead(UUID.randomUUID(), "a@x.com", "+1", "A", "NEW");
        Lead lead2 = new Lead(UUID.randomUUID(), "b@x.com", "+2", "B", "NEW");
        assertThat(lead1.id()).isNotEqualTo(lead2.id());
    }

    @Test
    void shouldReturnEmail_whenAccessed() {
        UUID uuid = UUID.randomUUID();
        Lead lead = new Lead(uuid, "test@example.com", "+71234567890", "TestCorp", "NEW");
        assertThat(lead.email()).isEqualTo("test@example.com");
    }

    @Test
    void shouldReturnPhone_whenAccessed() {
        UUID uuid = UUID.randomUUID();
        Lead lead = new Lead(uuid, "test@example.com", "+71234567890", "TestCorp", "NEW");
        assertThat(lead.phone()).isEqualTo("+71234567890");
    }

    @Test
    void shouldReturnCompany_whenAccessed() {
        UUID uuid = UUID.randomUUID();
        Lead lead = new Lead(uuid, "test@example.com", "+71234567890", "TestCorp", "NEW");
        assertThat(lead.company()).isEqualTo("TestCorp");
    }

    @Test
    void shouldReturnStatus_whenAccessed() {
        UUID uuid = UUID.randomUUID();
        Lead lead = new Lead(uuid, "test@example.com", "+71234567890", "TestCorp", "NEW");
        assertThat(lead.status()).isEqualTo("NEW");
    }
}