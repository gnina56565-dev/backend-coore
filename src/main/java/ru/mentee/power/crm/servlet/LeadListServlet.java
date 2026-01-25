//package ru.mentee.power.crm.servlet;
//
//import gg.jte.ContentType;
//import gg.jte.TemplateEngine;
//import gg.jte.output.StringOutput;
//import gg.jte.resolve.DirectoryCodeResolver;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import ru.mentee.power.crm.model.Lead;
//import ru.mentee.power.crm.service.LeadService;
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@WebServlet("/leads")
//public class LeadListServlet extends HttpServlet {
//    private TemplateEngine templateEngine;
//
//    @Override
//    public void init() throws ServletException {
//        Path templatePath = Path.of("src/main/jte").toAbsolutePath();
//        DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(templatePath);
//        this.templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//            LeadService service = (LeadService) getServletContext().getAttribute("leadService");
//            List<Lead> leads = service.findAll();
//
//            Map<String, Object> model = new HashMap<>();
//            model.put("leads", leads);
//
//            response.setContentType("text/html; charset=UTF-8");
//
//            StringOutput output = new StringOutput();
//            templateEngine.render("leads/list.jte", model, output);
//            String html = output.toString();
//            response.getWriter().write(html);
//    }
//}