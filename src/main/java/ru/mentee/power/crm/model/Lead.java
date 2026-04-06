package ru.mentee.power.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Entity
@Table(name = "leads")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(exclude = "company")
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotNull(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2  до 100 символов")
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    @NotNull(message = "Статус обязателен")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadStatus status;
    @Version
    @Column(name = "version", nullable = false)
    @Setter(AccessLevel.NONE)
    private Long version;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    public Lead(String email, Company company, LeadStatus status) {
        this.email = email;
        this.company = company;
        this.status = status;
    }

    public Lead(UUID uuid, String email, Company company, LeadStatus status) {
        this.id = uuid;
        this.email = email;
        this.company = company;
        this.status = status;
    }
    public Lead(String email, LeadStatus status) {
        this.email = email;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
    @Transient
    private String companyName;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}