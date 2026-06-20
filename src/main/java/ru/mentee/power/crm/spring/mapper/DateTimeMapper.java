package ru.mentee.power.crm.spring.mapper;

import org.mapstruct.Mapper;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public class DateTimeMapper {

  public OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
    if (localDateTime == null) {
      return null;
    }
    return localDateTime.atZone(ZoneOffset.systemDefault()).toOffsetDateTime();
  }
}
