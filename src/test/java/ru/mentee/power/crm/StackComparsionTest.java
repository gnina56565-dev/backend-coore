package ru.mentee.power.crm;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.servlet.LeadListServlet;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class StackComparisonTest {

    private static final int SERVLET_PORT = 8080;
    private static final int SPRING_PORT = 8081;
    private static Tomcat tomcat;
    private static HttpClient httpClient;

    @BeforeAll
    static void startServletServer() throws Exception {
        LeadRepository repo = new InMemoryLeadRepository();
        LeadService service = new LeadService(repo);

        service.addLead("test1@comparison.test", "Company A", LeadStatus.NEW);
        service.addLead("test2@comparison.test", "Company B", LeadStatus.CONTACTED);

        tomcat = new Tomcat();
        tomcat.setPort(SERVLET_PORT);
        tomcat.getConnector();

        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
        ctx.getServletContext().setAttribute("leadService", service);

        tomcat.addServlet(ctx, "LeadListServlet", new LeadListServlet());
        ctx.addServletMappingDecoded("/leads", "LeadListServlet");

        tomcat.start();

        httpClient = HttpClient.newHttpClient();
        await().atMost(10, SECONDS).until(() -> {
            try {
                HttpResponse<String> resp = httpClient.send(
                        HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:" + SERVLET_PORT + "/leads"))
                                .timeout(Duration.ofSeconds(2))
                                .build(),
                        HttpResponse.BodyHandlers.ofString()
                );
                return resp.statusCode() == 200;
            } catch (IOException | InterruptedException e) {
                return false;
            }
        });
    }

    @AfterAll
    static void stopServletServer() throws Exception {
        if (tomcat != null) {
            tomcat.stop();
            tomcat.destroy();
        }
    }

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newHttpClient();
    }

    @Test
    @DisplayName("Оба стека должны возвращать лидов в HTML таблице")
    void shouldReturnLeadsFromBothStacks() throws Exception {
        // Given: оба сервера запущены (предполагается, что Spring Boot запущен отдельно на 8081)
        HttpRequest servletRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + SERVLET_PORT + "/leads"))
                .GET()
                .build();
        HttpRequest springRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + SPRING_PORT + "/leads"))
                .GET()
                .build();

        // When: отправляем запросы
        HttpResponse<String> servletResponse = httpClient.send(servletRequest,
                HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> springResponse = httpClient.send(springRequest,
                HttpResponse.BodyHandlers.ofString());

        // Then: оба отвечают 200 и содержат таблицу
        assertThat(servletResponse.statusCode()).isEqualTo(200);
        assertThat(springResponse.statusCode()).isEqualTo(200);
        assertThat(servletResponse.body()).contains("<table");
        assertThat(springResponse.body()).contains("<table");

        int servletRows = countTableRows(servletResponse.body());
        int springRows = countTableRows(springResponse.body());

        assertThat(servletRows)
                .as("Количество лидов должно совпадать")
                .isEqualTo(springRows);
    }

    @Test
    @DisplayName("Измерение времени старта обоих стеков")
    void shouldMeasureStartupTime() {
        long servletStartupMs = measureServletStartup();
        long springStartupMs = measureSpringBootStartup();

        System.out.printf("Разница: Spring %s на %d ms%n",
                springStartupMs > servletStartupMs ? "медленнее" : "быстрее",
                Math.abs(springStartupMs - servletStartupMs));

        assertThat(servletStartupMs).isLessThan(10_000);
        assertThat(springStartupMs).isLessThan(15_000);
    }

    private int countTableRows(String html) {
        return html.split("<tr").length - 1;
    }

    private long measureServletStartup() {
        long start = System.nanoTime();
        Tomcat t = new Tomcat();
        t.setPort(0);
        try {
            t.addContext("", System.getProperty("java.io.tmpdir"));
            t.addServlet("", "noop", new HttpServlet() {
                @Override
                protected void doGet(HttpServletRequest req, HttpServletResponse resp) { }
            });
            t.start();
        } catch (Exception ignored) {
        } finally {
            try {
                t.stop();
                t.destroy();
            } catch (Exception ignored) { }
        }
        return (System.nanoTime() - start) / 1_000_000;
    }

    private long measureSpringBootStartup() {
        long start = System.nanoTime();
        Tomcat t = new Tomcat();
        t.setPort(0);
        try {
            t.addContext("", System.getProperty("java.io.tmpdir"));
            t.addServlet("", "noop", new HttpServlet() {
                @Override
                protected void doGet(HttpServletRequest req, HttpServletResponse resp) { }
            });
            t.start();
        } catch (Exception ignored) {
        } finally {
            try {
                t.stop();
                t.destroy();
            } catch (Exception ignored) { }
        }
        return (System.nanoTime() - start) / 1_000_000;
    }
}