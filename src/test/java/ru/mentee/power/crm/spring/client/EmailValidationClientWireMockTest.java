package ru.mentee.power.crm.spring.client;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@WireMockTest(httpPort = 8089)
@SpringBootTest
class EmailValidationClientWireMockTest {

    @Autowired
    private EmailValidationClient emailValidationClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("email.validation.base-url", () -> "http://localhost:8089");
    }

    @Test
    void shouldReturnValid_whenEmailIsCorrect() {
        stubFor(get(urlPathEqualTo("/api/validate/email"))
                .withQueryParam("email", equalTo("john@example.com"))
                .willReturn(okJson("""
                {
                    "email": "john@example.com",
                    "valid": true,
                    "reason": "Email exists"
                }
                """)));

        EmailValidationResponse response = emailValidationClient.validateEmail("john@example.com");

        assertThat(response).isNotNull();
        assertThat(response.valid()).isTrue();
        assertThat(response.email()).isEqualTo("john@example.com");
    }

    @Test
    void shouldReturnInvalid_whenEmailIsIncorrect() {
        stubFor(get(urlPathEqualTo("/api/validate/email"))
                .withQueryParam("email", equalTo("invalid-email"))
                .willReturn(okJson("""
                {
                    "email": "invalid-email",
                    "valid": false,
                    "reason": "Invalid email format"
                }
                """)));

        EmailValidationResponse response = emailValidationClient.validateEmail("invalid-email");

        assertThat(response).isNotNull();
        assertThat(response.valid()).isFalse();
        assertThat(response.reason()).isEqualTo("Invalid email format");
    }

    @Test
    void shouldHandleServerError_whenExternalServiceFails() {
        stubFor(get(urlPathEqualTo("/api/validate/email"))
                .willReturn(serverError().withBody("Internal Server Error")));
        assertThatThrownBy(() -> emailValidationClient.validateEmail("test@example.com"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldHandleTimeout_whenExternalServiceIsSlow() {
        stubFor(get(urlPathEqualTo("/api/validate/email"))
                .willReturn(okJson("{\"valid\": true}")
                        .withFixedDelay(15000)));

        assertThatThrownBy(() ->
                assertTimeoutPreemptively(Duration.ofSeconds(20), () ->
                        emailValidationClient.validateEmail("slow@example.com")
                )
        ).isInstanceOf(RuntimeException.class);
    }
}