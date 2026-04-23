package ru.mentee.power.crm;

import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Упрощенная версия сервиса только для Servlet-стека
public class SimpleLeadService {
    private final InMemoryLeadRepository repository;

    public SimpleLeadService(InMemoryLeadRepository repository) {
        this.repository = repository;
    }

    public void addLead(String email, String companyName, LeadStatus status) {
        // Создаем компанию "на лету" без сохранения в БД (так как нет CompanyRepository)
        Company company = new Company(companyName, "General");
        Lead lead = new Lead(email, company, status);
        repository.save(lead);
    }

    public List<Lead> findAll() {
        return repository.findAll();
    }

    public Optional<Lead> findById(UUID id) {
        return repository.findById(id);
    }

    // Добавьте другие методы, если они нужны сервлету
}