package ru.mentee.power.crm.spring.service;

import org.springframework.stereotype.Service;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.LeadRepository;  // ← ПРАВИЛЬНЫЙ импорт!

import java.util.List;

@Service
public class LeadService {

    private final LeadRepository repository;

    public LeadService(LeadRepository repository) {
        this.repository = repository;
    }

    public List<Lead> findAll() {
        return repository.findAll();  
    }
}
