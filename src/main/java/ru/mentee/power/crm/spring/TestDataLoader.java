    package ru.mentee.power.crm.spring;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

@Configuration
public class TestDataLoader {

    @Bean
    public ApplicationRunner loadData(LeadService leadService) {
        return args -> {
            leadService.addLead("Ex@t.com", "Company A", LeadStatus.NEW);
            leadService.addLead("Exa@te.com", "Company B", LeadStatus.CONTACTED);
            leadService.addLead("Exam@tes.com", "Company C", LeadStatus.NEW);
            leadService.addLead("Examp@test.com", "Company D", LeadStatus.CONTACTED);
            leadService.addLead("Example@test.com", "Company E", LeadStatus.NEW);
        };
    }
}