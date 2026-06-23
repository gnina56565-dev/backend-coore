package ru.mentee.power.crm.spring.rest.gate4;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
@Table(name = "employees")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column
    private BigDecimal salary;
    @Column(nullable = false)
    private String name;

    public Employee(String name, BigDecimal salary) {
        this.name = name;
        this.salary = salary;
    }
}
