//package ru.mentee.power.crm;
//
//import org.apache.catalina.Context;
//import org.apache.catalina.startup.Tomcat;
//import ru.mentee.power.crm.model.LeadStatus;
//import ru.mentee.power.crm.repository.InMemoryLeadRepository;
//import ru.mentee.power.crm.repository.LeadRepository;
//import ru.mentee.power.crm.service.LeadService;
//import ru.mentee.power.crm.servlet.LeadListServlet;
//
//import java.io.File;
//
//public class Main {
//    public static void main(String[] args) throws Exception {
//        LeadRepository leadRepository = new InMemoryLeadRepository();
//        LeadService leadService = new LeadService(leadRepository);
//
//        leadService.addLead("Ex@t.com", "Compnay A", LeadStatus.NEW);
//        leadService.addLead("Exa@te.com", "Compnay B", LeadStatus.CONTACTED);
//        leadService.addLead("Exam@tes.com", "Compnay C", LeadStatus.NEW);
//        leadService.addLead("Examp@test.com", "Compnay D", LeadStatus.CONTACTED);
//        leadService.addLead("Example@test.com", "Compnay E", LeadStatus.NEW);
//
//        Tomcat tomcat = new Tomcat();
//        tomcat.setPort(8080);
//        tomcat.getConnector();
//        Context context = tomcat.addContext("", new File(".").getAbsolutePath());
//        context.getServletContext().setAttribute("leadService", leadService);
//
//        tomcat.addServlet(context, "LeadListServlet", new LeadListServlet());
//        context.addServletMappingDecoded("/leads", "LeadListServlet");
//        tomcat.start();
//
//        System.out.println("Tomcat started on port 8080");
//        System.out.println("Open http://localhost:8080/leads in browser");
//
//        tomcat.getServer().await();
//    }
//}