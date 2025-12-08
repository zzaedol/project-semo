package com.semo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "warehouse")
public class Warehouse extends BasicEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "owner_id", nullable = false)
  private Member owner;

  @Column(name = "title")
  private String title;

  @Column(name = "description", length = Integer.MAX_VALUE)
  private String description;

  @Column(name = "address", nullable = false, length = 500)
  private String address;

  @Column(name = "area_sqm", nullable = false, precision = 10, scale = 2)
  private BigDecimal areaSqm;

  @Column(name = "price_per_month", precision = 10, scale = 2)
  private BigDecimal pricePerMonth;

  @Column(name = "available_status", nullable = false)
  @Builder.Default
  private Boolean availableStatus = false;

  @Column(name = "latitude")
  private Double latitude;

  @Column(name = "longitude")
  private Double longitude;

  @Column(name = "image_path")
  private String imagePath;

}