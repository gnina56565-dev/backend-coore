package ru.mentee.power.crm.spring.repository;

import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LeadRepository {
    private static final List<Lead> LEADS = List.of(
            new Lead(UUID.randomUUID(), new Contact("Ex@t.com", "+1", new Address("St1","City1","123")), "Compnay A"),
            new Lead(UUID.randomUUID(), new Contact("Exa@te.com", "+2", new Address("St2","City2","456")), "Compnay B"),
            new Lead(UUID.randomUUID(), new Contact("Exam@tes.com", "+3", new Address("St3","City3","789")), "Compnay C"),
            new Lead(UUID.randomUUID(), new Contact("Examp@test.com", "+4", new Address("St4","City4","012")), "Compnay D"),
            new Lead(UUID.randomUUID(), new Contact("Example@test.com", "+5", new Address("St5","City5","345")), "Compnay E")
    );

    public List<Lead> findAll() {
        return LEADS;
    }

    public Optional<Lead> findById(UUID id) {
        return LEADS.stream().filter(lead -> lead.id().equals(id)).findFirst();
    }

    public Lead save(Lead lead) {
        return lead;
    }
}
