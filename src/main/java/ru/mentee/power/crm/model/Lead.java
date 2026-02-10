package ru.mentee.power.crm.model;
import java.util.Objects;
import java.util.UUID;

public class Lead {
    private UUID id;
    private String email;
    private String company;
    private LeadStatus status;

    public Lead() {
    }

    public Lead(UUID id, String email, String company, LeadStatus status) {
        this.id = id;
        this.email = email;
        this.company = company;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getCompany() {
        return company;
    }

    public LeadStatus getStatus() {
        return status;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setStatus(LeadStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lead lead = (Lead) o;
        return Objects.equals(id, lead.id) &&
                Objects.equals(email, lead.email) &&
                Objects.equals(company, lead.company) &&
                status == lead.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, company, status);
    }

    @Override
    public String toString() {
        return "Lead{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", company='" + company + '\'' +
                ", status=" + status +
                '}';
    }
}
