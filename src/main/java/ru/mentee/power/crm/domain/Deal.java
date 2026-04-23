package ru.mentee.power.crm.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mentee.power.crm.entity.DealProduct;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "deals")
@NoArgsConstructor
@AllArgsConstructor
public class Deal {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Version
	@Column(nullable = false)
	private Long version;

	@Column(nullable = false)
	private UUID leadId;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DealStatus status;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "deal", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DealProduct> dealProducts = new ArrayList<>();

	public Deal(UUID leadId, BigDecimal amount) {
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
			throw new IllegalStateException("Cannot transition from " + this.status + " to " + newStatus);
		}
		this.status = newStatus;
	}

	public void setId(UUID uuid) {
		this.id = uuid;
	}

	public void addDealProduct(DealProduct dealProduct) {
		dealProducts.add(dealProduct);
		dealProduct.setDeal(this);
	}

	public void removeDealProduct(DealProduct dealProduct) {
		dealProducts.remove(dealProduct);
		dealProduct.setDeal(null);
	}
}
