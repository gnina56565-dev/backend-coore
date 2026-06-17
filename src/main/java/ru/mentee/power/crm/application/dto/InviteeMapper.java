package ru.mentee.power.crm.application.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.mentee.power.crm.model.Lead;

@Component
@Mapper(componentModel = "spring")
public interface InviteeMapper {

  @Mapping(source = "id", target = "id")
  @Mapping(source = "email", target = "email")
  @Mapping(target = "firstName", ignore = true)
  default InviteeResponse toResponse(Lead lead) {
    return null;
  }
}
