package com.semo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment")
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "contract_id", nullable = false)
  private Contract contract;

  @Column(name = "amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @Column(name = "payment_method", length = 50)
  private String paymentMethod;

  @Column(name = "payment_status", nullable = false, length = 50)
  private String paymentStatus;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "paid_at")
  private LocalDateTime paidAt;

}