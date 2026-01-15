package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LeadTest {

    @Test
    void shouldCreateLead_whenValidData() {
        Address address = new Address("Moscow", "Kuybisheva St", "94105");
        Contact contact = new Contact("goga@gmail.com", "+79374810224", address);
        Lead lead = new Lead(UUID.randomUUID(), contact, "Gora", "NEW");
        assertThat(lead.contact()).isEqualTo(contact);
    }

    @Test
    void shouldAccessEmailThroughDelegation_whenLeadCreated() {
        Address address = new Address("Moscow", "Kuybisheva St", "94105");
        Contact contact = new Contact("goga@gmail.com", "+79374810224", address);
        Lead lead = new Lead(UUID.randomUUID(), contact, "Gora", "NEW");
        assertThat(lead.contact().email()).isEqualTo("goga@gmail.com");
        assertThat(lead.contact().address().city()).isEqualTo("Moscow");
    }

    @Test
    void shouldBeEqual_whenSameIdButDifferentContact() {
        UUID id = UUID.randomUUID();
        Address addressOne = new Address("Moscow", "Kuybisheva St", "94105");
        Contact contactOne = new Contact("goga@gmail.com", "+79374810224", addressOne);
        Lead leadOne = new Lead(id, contactOne, "Gora", "NEW");

        Address addressTwo = new Address("Moscow", "Novi St", "6501");
        Contact contactTwo = new Contact("vova@gmail.com", "+79374814022", addressTwo);
        Lead leadTwo = new Lead(id, contactTwo, "Skala", "NEW");

        assertThat(leadOne).isNotEqualTo(leadTwo);
    }

    @Test
    void shouldThrowException_whenContactIsNull() {
        assertThatThrownBy(() -> new Lead (UUID.randomUUID(), null,
                "Gora", "NEW"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test

    void shouldReturnFormattedString_whenToStringCalled() {
        Lead lead = new Lead("L1", "test@example.com",
                "+71234567890", "TestCorp", "NEW");
        String toString = lead.toString();
        assertThat(toString).isEqualTo("Lead{id='L1', email='test@example.com'," +
                " phone='+71234567890', company='TestCorp', status='NEW'}");

    void shouldThrowException_whenInvalidStatus() {
        Address address = new Address("Moscow", "Kuybisheva St", "94105");
        Contact contact = new Contact("goga@gmail.com", "+79374810224", address);
        assertThatThrownBy(() -> new Lead (UUID.randomUUID(), contact,
                "Gora", "INVALID"))
                .isInstanceOf(IllegalArgumentException.class);


    }

    @Test
    void shouldDemonstrateThreeLevelComposition_whenAccessingCity() {
        Address addressNum = new Address("Moscow", "Kuybisheva St", "94105");
        Contact contactNum = new Contact("goga@gmail.com", "+79374810224", addressNum);
        Lead lead = new Lead(UUID.randomUUID(), contactNum, "Gora", "NEW");
        Contact contact = lead.contact();
        Address address = contact.address();
        String city = address.city();
        assertThat(city).isEqualTo("Moscow");
    }
}