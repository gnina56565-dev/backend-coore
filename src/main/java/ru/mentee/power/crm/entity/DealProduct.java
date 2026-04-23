package ru.mentee.power.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.model.Product;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "deal_product")
public class DealProduct {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  @JoinColumn(name = "deal_id")
  @ManyToOne
  private Deal deal;
  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;
  @Column(nullable = false)
  private Integer quantity;
  @Column(name = "unit_price", precision = 15, scale = 2)
  private BigDecimal unitPrice;

}
