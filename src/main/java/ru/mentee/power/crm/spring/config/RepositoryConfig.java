package ru.mentee.power.crm.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.repository.LeadRepository;

@Configuration
public class RepositoryConfig {
    @Bean
    public LeadRepository leadRepository() {
        return new InMemoryLeadRepository();
    }
}