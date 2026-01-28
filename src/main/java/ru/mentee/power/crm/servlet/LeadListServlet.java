package ru.mentee.power.crm.servlet;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.service.LeadService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LeadListServlet extends HttpServlet {
    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        try {
            Path templatePath = Path.of("src/main/jte").toAbsolutePath();
            DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(templatePath);
            this.templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);

        } catch (Exception e) {
            throw new ServletException("Failed to initialize template engine", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            LeadService service = (LeadService) getServletContext().getAttribute("leadService");
            List<Lead> leads = service.findAll();

            Map<String, Object> model = new HashMap<>();
            model.put("leads", leads);

            response.setContentType("text/html; charset=UTF-8");

            StringOutput output = new StringOutput();
            templateEngine.render("servlet/leads/list.jte", model, output);
            String html = output.toString();

            if (html.trim().startsWith("@") || html.contains("TemplateNotFoundException")) {
                response.sendError(500, "JTE template rendering failed: " + html.substring(0, Math.min(200, html.length())));
                return;
            }

            response.getWriter().write(html);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error rendering template: " + e.getMessage());
        }
    }
}