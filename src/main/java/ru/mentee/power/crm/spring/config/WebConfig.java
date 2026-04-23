package ru.mentee.power.crm.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.mentee.power.crm.model.Company;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(String.class, Company.class, source -> {
			if (source == null || source.trim().isEmpty()) {
				return null;
			}
			return new Company(source.trim(), null);
		});
	}
}
