package ru.mentee.power.crm.spring.service;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@WireMockTest
class LeadServiceRetryTest {

  @Autowired
  private LeadService leadService;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry, WireMockRuntimeInfo wmRuntimeInfo) {
    registry.add("email.validation.base-url", wmRuntimeInfo::getHttpBaseUrl);
    registry.add("resilience4j.retry.instances.email-validation.wait-duration", () -> "100ms");
  }

  @Test
  void shouldRetryAndSucceed_whenFirstAttemptFails() {
    stubFor(get(urlPathEqualTo("/api/validate/email")).inScenario("Retry Test").whenScenarioStateIs(Scenario.STARTED)
        .willReturn(serverError()).willSetStateTo("First Retry"));

    stubFor(get(urlPathEqualTo("/api/validate/email")).inScenario("Retry Test").whenScenarioStateIs("First Retry")
        .willReturn(serverError()).willSetStateTo("Second Retry"));

    stubFor(get(urlPathEqualTo("/api/validate/email")).inScenario("Retry Test").whenScenarioStateIs("Second Retry")
        .willReturn(okJson("""
            {"email": "test@example.com", "valid": true, "reason": "OK"}
            """)));
    Company company = new Company("Test Corp", "IT");
    Lead lead = new Lead("test@example.com", company, LeadStatus.NEW);

    Lead created = leadService.createLead(lead);
    assertThat(created).isNotNull();

    verify(3, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }

  @Test
  void shouldUseFallback_whenAllRetriesFail() {
    stubFor(get(urlPathEqualTo("/api/validate/email")).willReturn(serverError().withBody("Service Unavailable")));
    Company company = new Company("Test Corp", "IT");
    Lead lead = new Lead("test@example.com", company, LeadStatus.NEW);

    Lead created = leadService.createLead(lead);

    assertThat(created).isNotNull();

    verify(3, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }

  @Test
  void shouldNotRetry_whenClientErrorOccurs() {
    stubFor(get(urlPathEqualTo("/api/validate/email"))
        .willReturn(badRequest().withBody("{\"error\": \"Invalid format\"}")));
    Company company = new Company("Test Corp", "IT");
    Lead lead = new Lead("invalid", company, LeadStatus.NEW);

    assertThatThrownBy(() -> leadService.createLead(lead)).isInstanceOf(feign.FeignException.BadRequest.class);

    verify(1, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }

  @Test
  void shouldRetry_whenTimeoutOccurs() {
    stubFor(get(urlPathEqualTo("/api/validate/email")).inScenario("Timeout Retry").whenScenarioStateIs(Scenario.STARTED)
        .willReturn(ok().withFixedDelay(10000)).willSetStateTo("After Timeout"));

    stubFor(get(urlPathEqualTo("/api/validate/email")).inScenario("Timeout Retry").whenScenarioStateIs("After Timeout")
        .willReturn(okJson("""
            {"email": "test@example.com", "valid": true, "reason": "OK"}
            """)));
    Company company = new Company("Test Corp", "IT");
    Lead lead = new Lead("test@example.com", company, LeadStatus.NEW);

    Lead created = leadService.createLead(lead);
    assertThat(created).isNotNull();
    verify(2, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }
}
