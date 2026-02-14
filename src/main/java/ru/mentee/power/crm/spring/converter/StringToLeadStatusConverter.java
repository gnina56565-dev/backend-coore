package ru.mentee.power.crm.spring.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.mentee.power.crm.model.LeadStatusNew;

@Component
public class StringToLeadStatusConverter implements Converter<String, LeadStatusNew> {
    @Override
    public LeadStatusNew convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return LeadStatusNew.fromString(source);
    }
}