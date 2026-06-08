package ru.mentee.power.crm.spring.mapper;
import org.mapstruct.Mapper;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface DateTimeMapper {

  default OffsetDateTime map(LocalDateTime value) {
    if (value == null) {
      return null;
    }

    return value.atZone(ZoneOffset.systemDefault()).toOffsetDateTime();
  }

  default LocalDateTime map(OffsetDateTime value) {
    if (value == null) {
      return null;
    }
    return value.toLocalDateTime();
  }
}
