package ru.mentee.power.crm.domain;

public record Contact(String email, String phone, Address address) {
    public Contact {
        if (email == null) {
            throw new IllegalArgumentException();
        }
        if (phone == null) {
            throw new IllegalArgumentException();
        }
        if (address == null) {
            throw new IllegalArgumentException();
        }
    }
}

