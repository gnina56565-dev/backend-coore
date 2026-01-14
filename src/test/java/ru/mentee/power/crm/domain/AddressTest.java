package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class AddressTest {
    @Test
    void shouldCreateAddress_whenValidData() {
        Address address = new Address("San Francisco",
                "123 Main St", "94105");
        assertThat(address.city()).isEqualTo("San Francisco");
        assertThat(address.street()).isEqualTo("123 Main St");
        assertThat(address.zip()).isEqualTo("94105");
    }

    @Test
    void shouldBeEqual_whenSameData() {
        Address addressOne = new Address("San Francisco",
                "123 Main St", "94105");
        Address addressTwo = new Address("San Francisco",
                "123 Main St", "94105");
        addressTwo.equals(addressOne);
        addressOne.equals(addressTwo);
        assertThat(addressOne.hashCode()).isEqualTo(addressTwo.hashCode());
    }

    @Test
    void shouldThrowException_whenCityIsNull() {
        assertThatThrownBy(() -> new Address (null,
                "123 Main St", "94105"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowException_whenZipIsBlank() {
        assertThatThrownBy(() -> new Address ("San Francisco",
                "123 Main St", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
