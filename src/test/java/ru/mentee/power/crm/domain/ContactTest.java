package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ContactTest {

    @Test
    void shouldCreateContact_whenValidData() {
        Contact contact = new Contact("John", "Doe", "john@example.com");
        assertThat(contact.firstName()).isEqualTo("John");
        assertThat(contact.lastName()).isEqualTo("Doe");
        assertThat(contact.email()).isEqualTo("john@example.com");
    }

    @Test
    void shouldBeEqual_whenSameData() {
        Contact contactOne = new Contact("John", "Doe", "john@example.com");
        Contact contactTwo = new Contact("John", "Doe", "john@example.com");
        assertThat(contactOne).isEqualTo(contactTwo);
        assertThat(contactOne.hashCode()).isEqualTo(contactTwo.hashCode());
    }

    @Test
    void shouldNotBeEqual_whenDifferentData() {
        Contact contactOne = new Contact("John", "Doe", "john@example.com");
        Contact contactTwo = new Contact("Kate", "Grue", "kate@example.com");
        assertThat(contactOne).isNotEqualTo(contactTwo);
    }
}