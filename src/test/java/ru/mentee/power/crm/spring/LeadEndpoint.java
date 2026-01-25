package ru.mentee.power.crm.spring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LeadEndpoint {

    @LocalServerPort
    int port;

    RestTemplate restTemplate = new RestTemplate();

    @Test
    void leadsPageReturnsHtmlWithTable() {
        String url = "http://localhost:" + port + "/leads";
        ResponseEntity<String> response =
                restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        String body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body).contains("Leads (Spring Boot");
        assertThat(body).contains("Compnay A");
        assertThat(body).contains("<table");
    }
}
