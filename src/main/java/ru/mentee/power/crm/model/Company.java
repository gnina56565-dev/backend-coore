package ru.mentee.power.crm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "companies")
@ToString(exclude = "leads")
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  String name;

  String industry;

  @JsonIgnore
  @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Lead> leads = new ArrayList<>();

  public Company(String name, String industry) {
    this.name = name;
    this.industry = industry;
  }

  public void addLead(Lead lead) {
    leads.add(lead);
    lead.setCompany(this);
  }
  public void removeLead(Lead lead) {
    leads.remove(lead);
    lead.setCompany(null);
  }
}
