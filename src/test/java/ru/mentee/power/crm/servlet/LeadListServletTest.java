package ru.mentee.power.crm.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LeadListServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletContext servletContext;

    @Mock
    private ServletConfig servletConfig;

    private LeadListServlet servlet;
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new LeadListServlet();

        stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        LeadService mockService = mock(LeadService.class);
        when(servletContext.getAttribute("leadService")).thenReturn(mockService);

        List<Lead> leads = List.of(
                new Lead(UUID.randomUUID(), "test@example.com", "Test Company", LeadStatus.NEW)
        );
        when(mockService.findAll()).thenReturn(leads);

        when(servletConfig.getServletContext()).thenReturn(servletContext);
        servlet.init(servletConfig);
    }

    @Test
    void doGet_ShouldGenerateHtmlTableWithLeads() throws Exception {
        servlet.doGet(request, response);

        String output = stringWriter.toString();
        assert output.contains("<table");
        assert output.contains("<th>Email</th>");
        assert output.contains("<td>test@example.com</td>");
        assert output.contains("<td>Test Company</td>");
        assert output.contains("<td>NEW</td>");
        assert output.contains("</html>");
    }
}