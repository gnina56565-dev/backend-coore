package ru.mentee.power.crm.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

public class LeadSpecifications {
  public static Specification<Lead> hasEmailContaining(String email) {
    return (root, query, cb) -> {
      if (email == null || email.isBlank()) {
        return cb.conjunction();
      }
      return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    };
  }

  public static Specification<Lead> hasStatus(LeadStatus status) {
    return (root, query, cb) -> {
      if (status == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("status"), status);
    };
  }

  public static Specification<Lead> buildFilter(String email, LeadStatus status) {
    return hasEmailContaining(email).and(hasStatus(status));
  }
}
