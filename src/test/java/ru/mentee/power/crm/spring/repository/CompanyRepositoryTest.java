package ru.mentee.power.crm.spring.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveCompanyWithLeads() {
        Company company = new Company("Т-банк", "Finance");

        Lead lead1 = new Lead("ivan@sber.ru", LeadStatus.NEW);
        Lead lead2 = new Lead("maria@sber.ru", LeadStatus.CONTACTED);
        Lead lead3 = new Lead("pefro@am.com", LeadStatus.QUALIFIED);

        company.addLead(lead1);
        company.addLead(lead2);
        company.addLead(lead3);

        Company saved = companyRepository.save(company);

        assertThat(saved.getId()).isNotNull();

        assertThat(saved)
                .extracting(Company::getLeads)
                .asList()
                .hasSize(3);

        Company found = companyRepository.findByIdWithLeads(saved.getId()).orElseThrow();

        assertThat(found)
                .extracting(Company::getLeads)
                .asList()
                .hasSize(3);
    }

    @Test
    void shouldAvoidN1WithEntityGraph() {
        Company company = new Company("Тинькофф", "Finance");
        for (int i = 0; i < 5; i++) {
            company.addLead(new Lead("lead" + i + "@tinkoff.ru", LeadStatus.NEW));
        }
        Company saved = companyRepository.save(company);

        entityManager.clear();

        Company found = companyRepository.findByIdWithLeads(saved.getId()).orElseThrow();

        assertThat(found)
                .extracting(Company::getLeads)
                .asList()
                .hasSize(5);
    }
}