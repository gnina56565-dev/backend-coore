package ru.mentee.power.crm.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Deal {
    private final UUID id;
    private final UUID leadId;
    private final BigDecimal amount;
    private DealStatus status;
    private final LocalDateTime createdAt;

    public Deal(UUID leadId, BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.leadId = Objects.requireNonNull(leadId, "leadId must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.status = DealStatus.NEW;
        this.createdAt = LocalDateTime.now();
    }

    public Deal(UUID id, UUID leadId, BigDecimal amount, DealStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.leadId = leadId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void transitionTo(DealStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException("Cannot transition from X to Y");
        }
        this.status = newStatus;
    }

    public UUID getId() {
        return id;
    }

    public UUID getLeadId() {
        return leadId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public DealStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Deal deal = (Deal) o;
        return Objects.equals(id, deal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}