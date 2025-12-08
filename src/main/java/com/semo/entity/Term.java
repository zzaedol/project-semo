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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "terms")
public class Term extends BasicEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ColumnDefault("false")
  @Column(name = "agreeall", nullable = false)
  private Boolean agreeall = false;

  @ColumnDefault("false")
  @Column(name = "terms1", nullable = false)
  private Boolean terms1 = false;

  @ColumnDefault("false")
  @Column(name = "terms2", nullable = false)
  private Boolean terms2 = false;

  @ColumnDefault("false")
  @Column(name = "terms3", nullable = false)
  private Boolean terms3 = false;

  @ColumnDefault("false")
  @Column(name = "terms4", nullable = false)
  private Boolean terms4 = false;

  @ColumnDefault("false")
  @Column(name = "terms5", nullable = false)
  private Boolean terms5 = false;

}