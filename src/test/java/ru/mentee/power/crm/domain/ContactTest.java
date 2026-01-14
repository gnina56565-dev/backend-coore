
import static org.junit.jupiter.api.Assertions.*;
class ContactTest {
  

package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ContactTest {

    @Test
    void shouldCreateContact_whenValidData() {
        Address address = new Address("Moscow", "Veteranov St", "94105");
        Contact contact = new Contact("cherry@gmail.com", "+79274193771", address);
        assertThat(contact.address()).isEqualTo(address);
        assertThat(contact.address().city()).isEqualTo("Moscow");
    }

    @Test
    void shouldDelegateToAddress_whenAccessingCity() {
        Address address = new Address("Moscow", "Veteranov St", "94105");
        Contact contact = new Contact("cherry@gmail.com", "+79274193771", address);
        assertThat(contact.address().city()).isEqualTo("Moscow");
        assertThat(contact.address().street()).isEqualTo("Veteranov St");
    }

    @Test
    void shouldThrowException_whenAddressIsNull() {
        assertThatThrownBy(() -> new Contact ("ananas@gmail.com",
                "+79375910275", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}