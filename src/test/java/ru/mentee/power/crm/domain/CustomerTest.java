package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CustomerTest {

    @Test
    void shouldReuseContact_whenCreatingCustomer() {
        Address addressOne = new Address("Moscow", "Kuybisheva St", "94105");
        Address addressBilling = new Address("Samara", "Solnechnaya St", "675");
        Contact contact = new Contact("giraf@gmail.com", "+79364918234", addressOne);
        Customer customer = new Customer(UUID.randomUUID(), contact, addressBilling, "BRONZE");
        assertThat(customer.contact().address()).isNotEqualTo(customer.billingAddress());
        assertThat(addressOne.city()).isEqualTo("Moscow");
        assertThat(addressBilling.city()).isEqualTo("Samara");
    }

    @Test
    void shouldDemonstrateContactReuse_acrossLeadAndCustomer() {
        Address address = new Address("Moscow", "Kuybisheva St", "94105");
        Contact contact = new Contact("user@example.com", "+79991234567", address);
        Lead lead = new Lead(UUID.randomUUID(), contact, "Jacksy", "NEW");
        Customer customer = new Customer(UUID.randomUUID(), contact, address, "BRONZE");
        assertThat(lead.contact() == customer.contact());
    }
}