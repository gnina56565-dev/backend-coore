package ru.mentee.power.crm.spring.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BusinessException {

  private final String entityType;
  private final String entityId;

  public EntityNotFoundException(String entityType, String entityId) {
    super(HttpStatus.NOT_FOUND, String.format("%s not found with id: %s", entityType, entityId));
    this.entityType = entityType;
    this.entityId = entityId;
  }

  public EntityNotFoundException(String message, String entityType, String entityId) {
    super(message);
    this.entityType = entityType;
    this.entityId = entityId;
  }

  public String getEntityType() {
    return entityType;
  }

  public Object getEntityId() {
    return entityId;
  }
}
