package ru.mentee.power.crm.domain;

public class Lead {
    private String id;
    private String email;
    private String phone;
    private String company;
    private String status;

    // Конструктор
    public Lead (String id, String email, String phone, String company, String status) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.status = status;
    }
    // Getters
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

    // toString
    @Override
    public String toString() {
        return "Lead{id='" + id + "', Email'" + email +
                "', phone" + phone + "', company'"
                + company + "', status'" + status + "'}";
    }
}