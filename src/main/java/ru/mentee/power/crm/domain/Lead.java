package ru.mentee.power.crm.domain;

import java.util.Objects;

public class Lead {
    private String id;
    private String email;
    private String phone;
    private String company;
    private String status;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Lead lead = (Lead) o;
        return Objects.equals(id, lead.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public Lead (String id, String email, String phone, String company, String status) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.status = status;
    }

    public String getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phone;
    }
    public String getCompany() {
        return company;
    }
    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Lead{id='" + id + "', Email'" + email +
                "', phone" + phone + "', company'"
                + company + "', status'" + status + "'}";
    }
}