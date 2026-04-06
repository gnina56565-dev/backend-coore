package ru.mentee.power.crm;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.servlet.LeadListServlet;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        // 1. Создаем репозиторий
        InMemoryLeadRepository leadRepository = new InMemoryLeadRepository();

        // 2. Создаем УПРОЩЕННЫЙ сервис (который мы написали выше)
        // Он принимает всего 1 аргумент, как и ожидалось раньше
        SimpleLeadService leadService = new SimpleLeadService(leadRepository);

        // 3. Добавляем тестовые данные
        leadService.addLead("Ex@t.com", "Company A", LeadStatus.NEW);
        leadService.addLead("Exa@te.com", "Company B", LeadStatus.CONTACTED);
        leadService.addLead("Exam@tes.com", "Company C", LeadStatus.NEW);
        leadService.addLead("Examp@test.com", "Company D", LeadStatus.CONTACTED);
        leadService.addLead("Example@test.com", "Company E", LeadStatus.NEW);

        // 4. Настройка Tomcat
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        Context context = tomcat.addContext("", new File(".").getAbsolutePath());

        // Кладем в контекст наш простой сервис
        context.getServletContext().setAttribute("leadService", leadService);

        tomcat.addServlet(context, "LeadListServlet", new LeadListServlet());
        context.addServletMappingDecoded("/leads", "LeadListServlet");

        tomcat.start();

        System.out.println("Tomcat started on port 8080");
        System.out.println("Open http://localhost:8080/leads in browser");

        tomcat.getServer().await();
    }
}