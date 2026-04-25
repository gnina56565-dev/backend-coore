package ru.mentee.power.crm.specification; // Или ваш пакет

import org.springframework.data.jpa.domain.Specification;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

import java.util.List;

public class LeadSpecifications {

    /**
     * Динамический фильтр, объединяющий условия по Email, Статусу и Названию Компании.
     * Если параметр null или пустой, он игнорируется.
     */
    public static Specification<Lead> hasEmailAndStatusAndCompany(
            String emailPart,
            List<LeadStatus> statuses,
            String companyNamePart) {

        return (root, query, cb) -> {
            query.distinct(true); // Важно при JOIN, чтобы не было дублей
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            // 1. Фильтр по части Email (регистронезависимый LIKE)
            if (emailPart != null && !emailPart.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + emailPart.toLowerCase() + "%"));
            }

            // 2. Фильтр по списку Статусов (IN)
            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("status").in(statuses));
            }

            // 3. Фильтр по части Названия Компании (через JOIN)
            if (companyNamePart != null && !companyNamePart.isBlank()) {
                // Присоединяем сущность Company (поле 'company' в классе Lead)
                var companyJoin = root.join("company");
                predicates.add(cb.like(cb.lower(companyJoin.get("name")), "%" + companyNamePart.toLowerCase() + "%"));
            }

            // Объединяем все условия через AND
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}   