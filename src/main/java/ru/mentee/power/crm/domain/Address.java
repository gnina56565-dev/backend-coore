package ru.mentee.power.crm.domain;

public record Address(String city, String street, String zip) {
    public Address {
        if ((city == null || city.isBlank())) {
            throw new IllegalArgumentException();
        }
        if ((zip == null || zip.isBlank())) {
            throw new IllegalArgumentException();
        }
    }
}