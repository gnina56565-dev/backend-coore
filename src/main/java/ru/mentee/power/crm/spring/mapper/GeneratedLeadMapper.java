package ru.mentee.power.crm.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.AfterMapping;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.spring.dto.generated.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;
import ru.mentee.power.crm.spring.dto.generated.UpdateLeadRequest;
import ru.mentee.power.crm.spring.repository.CompanyRepository;

@Mapper(componentModel = "spring", uses = {DateTimeMapper.class})
public interface GeneratedLeadMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "companyName", source = "company")
  Lead toEntity(CreateLeadRequest dto);

  @Mapping(source = "company.name", target = "company")
  @Mapping(target = "firstName", ignore = true)
  @Mapping(target = "lastName", ignore = true)
  LeadResponse toResponse(Lead entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "companyName", source = "company")
  void updateEntity(UpdateLeadRequest dto, @MappingTarget Lead entity);

  @AfterMapping
  default void copyCompanyNameToCompany(Lead entity, @MappingTarget LeadResponse response) {
    if (entity.getCompany() != null) {
      response.setCompany(entity.getCompany().getName());
    } else if (entity.getCompanyName() != null) {
      response.setCompany(entity.getCompanyName());
    }
  }
}
