package ru.mentee.power.crm.spring.exception;

public class EntityNotFoundException extends BusinessException {

  private final String entityType;
  private final String entityId;

  public EntityNotFoundException(String entityType, String entityId) {
    super("Entity [" + entityType + "] with id [" + entityId + "] not found");
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

  public String getEntityID() {
    return entityId;
  }
}
