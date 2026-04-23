package ru.mentee.power.crm.spring.controller;

import ru.mentee.power.crm.spring.MockLeadService;
import ru.mentee.power.crm.spring.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class LeadControllerUnitTest {

	@Test
	void shouldCreateControllerWithoutSpring() {
		MockLeadService mockService = new MockLeadService();
		CompanyRepository mockRepository = Mockito.mock(CompanyRepository.class);
		LeadController controller = new LeadController(mockService, mockRepository);

		String response = controller.home();

		assertThat(response).contains("2 leads");
	}

	@Test
	void shouldUseInjectedService() {
		MockLeadService mockService = new MockLeadService();
		CompanyRepository mockRepository = Mockito.mock(CompanyRepository.class);

		LeadController controller = new LeadController(mockService, mockRepository);

		String response = controller.home();

		assertThat(response).isNotNull();
		assertThat(response).contains("Spring Boot CRM is running");
	}
}
