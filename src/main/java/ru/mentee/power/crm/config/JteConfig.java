package ru.mentee.power.crm.config;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.TemplateOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import java.io.Writer;
import java.nio.file.Path;
import java.util.Map;

@Configuration
public class JteConfig {

    @Value("${jte.templates.directory:src/main/jte}")
    private String templatesDirectory;

    @Bean
    @Profile("dev")
    public TemplateEngine jteDevTemplateEngine() {
        // Для разработки
        DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(Path.of(templatesDirectory));
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    @Bean
    @ConditionalOnMissingBean(name = "jteDevTemplateEngine")
    public TemplateEngine jteTemplateEngine() {
        // Для production (предкомпилированные шаблоны)
        return TemplateEngine.createPrecompiled(ContentType.Html);
    }

    @Bean
    public ViewResolver jteViewResolver(TemplateEngine templateEngine) {
        return new JteViewResolver(templateEngine);
    }

    public static class JteViewResolver extends AbstractTemplateViewResolver {

        private final TemplateEngine templateEngine;

        public JteViewResolver(TemplateEngine templateEngine) {
            this.templateEngine = templateEngine;
            setViewClass(JteView.class);
            setPrefix("/");
            setSuffix(".jte");
            setOrder(1); // Выше Thymeleaf, если он есть
        }

        @Override
        protected AbstractUrlBasedView instantiateView() {
            return new JteView(templateEngine);
        }
    }

    public static class JteView extends AbstractTemplateView {

        private final TemplateEngine templateEngine;

        public JteView(TemplateEngine templateEngine) {
            this.templateEngine = templateEngine;
        }

        @Override
        protected void renderMergedTemplateModel(
                Map<String, Object> model,
                HttpServletRequest request,
                HttpServletResponse response
        ) throws Exception {

            // Получаем имя шаблона (без префикса/суффикса)
            String viewName = getUrl();

            // Убираем ведущий слеш если есть
            if (viewName.startsWith("/")) {
                viewName = viewName.substring(1);
            }

            // Убираем расширение .jte если есть
            if (viewName.endsWith(".jte")) {
                viewName = viewName.substring(0, viewName.length() - 4);
            }

            // Устанавливаем кодировку
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");

            // Рендерим шаблон
            try (Writer writer = response.getWriter()) {
                templateEngine.render(viewName, (Object) model, (TemplateOutput) writer);
            }
        }
    }
}