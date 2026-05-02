package ru.mentee.power.crm.spring.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {EmailValidationFeignClientTest.Config.class})
class EmailValidationFeignClientTest {

    @Configuration
    @EnableAutoConfiguration
    @EnableFeignClients(clients = EmailValidationFeignClient.class)
    static class Config {
    }

    static WireMockServer wireMockServer = new WireMockServer(options().dynamicPort());

    @Autowired
    private EmailValidationFeignClient feignClient;

    @BeforeAll
    static void startWireMock() {
        wireMockServer.start();
        System.out.println("WireMock started on port: " + wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("email.validation.base-url", wireMockServer::baseUrl);
        registry.add("spring.cloud.compatibility-verifier.enabled", () -> "false");
    }

    @Test
    void shouldReturnValidResponse_whenEmailIsValid() {
        wireMockServer.stubFor(get(urlPathEqualTo("/api/validate/email"))
                .withQueryParam("email", equalTo("john@example.com"))
                .willReturn(okJson("""
                        {
                          "email": "john@example.com",
                          "valid": true,
                          "reason": "Email exists and is deliverable"
                        }
                        """)));

        EmailValidationResponse response = feignClient.validateEmail("john@example.com");

        assertThat(response.valid()).isTrue();
        assertThat(response.email()).isEqualTo("john@example.com");

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/api/validate/email"))
                .withQueryParam("email", equalTo("john@example.com")));
    }

    @Test
    void shouldReturnInvalidResponse_whenEmailIsInvalid() {
        wireMockServer.stubFor(get(urlPathEqualTo("/api/validate/email"))
                .withQueryParam("email", equalTo("invalid@bad.email"))
                .willReturn(okJson("""
                        {
                          "email": "invalid@bad.email",
                          "valid": false,
                          "reason": "Domain does not accept email"
                        }
                        """)));

        EmailValidationResponse response = feignClient.validateEmail("invalid@bad.email");

        assertThat(response.valid()).isFalse();
        assertThat(response.email()).isEqualTo("invalid@bad.email");
    }

    @Test
    void shouldThrowFeignException_whenExternalServiceReturns500() {
        wireMockServer.stubFor(get(urlPathEqualTo("/api/validate/email"))
                .willReturn(serverError().withBody("Internal Server Error")));

        assertThatThrownBy(() -> feignClient.validateEmail("any@email.com"))
                .isInstanceOf(feign.FeignException.class);
    }

    @Test
    void shouldThrowFeignException_whenExternalServiceReturns400() {
        wireMockServer.stubFor(get(urlPathEqualTo("/api/validate/email"))
                .willReturn(badRequest().withBody("{\"error\":\"Invalid email format\"}")));

        assertThatThrownBy(() -> feignClient.validateEmail("not-an-email"))
                .isInstanceOf(feign.FeignException.BadRequest.class);
    }
}