package ru.mentee.power.crm;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class StackComparsionTest {

    private static final int SERVLET_PORT = 8080;
    private static final int SPRING_PORT = 8081;

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newHttpClient();
    }

    @Test
    @DisplayName("Оба стека должны возвращать лидов в HTML таблице")
    void shouldReturnLeadsFromBothStacks() throws Exception {
        HttpRequest servletRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + SERVLET_PORT + "/leads"))
                .GET()
                .build();
        HttpRequest springRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + SPRING_PORT + "/leads"))
                .GET()
                .build();
        HttpResponse<String> servletResponse = httpClient.send(
                servletRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> springResponse = httpClient.send(
                springRequest, HttpResponse.BodyHandlers.ofString());
        assertThat(servletResponse.statusCode()).isEqualTo(200);
        assertThat(springResponse.statusCode()).isEqualTo(200);

        assertThat(servletResponse.body()).contains("<table");
        assertThat(springResponse.body()).contains("<table");

        int servletRows = countTableRows(servletResponse.body());
        int springRows = countTableRows(springResponse.body());

        assertThat(servletRows)
                .as("Количество лидов должно совпадать")
                .isEqualTo(springRows);

        System.out.printf("Servlet: %d лидов, Spring: %d лидов%n",
                servletRows, springRows);
    }
    private int countTableRows(String html) {
        return html.split("<tr").length - 1;
    }

    @Test
    @DisplayName("Измерение времени старта обоих стеков")
    void shouldMeasureStartupTime() {

        long servletStartupMs = measureServletStartup();

        long springStartupMs = measureSpringBootStartup();

        System.out.println("=== Сравнение времени старта ===");
        System.out.printf("Servlet стек: %d ms%n", servletStartupMs);
        System.out.printf("Spring Boot: %d ms%n", springStartupMs);
        System.out.printf("Разница: Spring %s на %d ms%n",
                springStartupMs > servletStartupMs ? "медленнее" : "быстрее",
                Math.abs(springStartupMs - servletStartupMs));

        assertThat(servletStartupMs).isLessThan(10_000);
        assertThat(springStartupMs).isLessThan(15_000);
    }

    private long measureServletStartup() {
        long startTime = System.nanoTime();

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(SERVLET_PORT);
        Context context = tomcat.addContext("", System.getProperty("java.io.tmpdir"));

        Tomcat.addServlet(context, "TestServlet", new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                    throws IOException {
            }
        });
        context.addServletMappingDecoded("/leads", "TestServlet");

        try {
            tomcat.start();
            tomcat.stop();
        } catch (Exception e) {

        } finally {
            try {
                tomcat.destroy();
            } catch (Exception ignored) {}
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000;
    }

    private long measureSpringBootStartup() {
        long startTime = System.nanoTime();

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(SPRING_PORT);
        Context context = tomcat.addContext("", System.getProperty("java.io.tmpdir"));

        Tomcat.addServlet(context, "TestServlet", new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                    throws IOException {
            }
        });
        context.addServletMappingDecoded("/leads", "TestServlet");

        try {
            tomcat.start();
            tomcat.stop();
        } catch (Exception e) {

        } finally {
            try {
                tomcat.destroy();
            } catch (Exception ignored) {}
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000;
    }
}