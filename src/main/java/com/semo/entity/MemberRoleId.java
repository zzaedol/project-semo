package com.semo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class MemberRoleId implements Serializable {

  private static final long serialVersionUID = 1907378199799548336L;
  @Column(name = "member_id", nullable = false)
  private Long memberId;

  @Column(name = "role_id", nullable = false)
  private Long roleId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    MemberRoleId entity = (MemberRoleId) o;
    return Objects.equals(this.roleId, entity.roleId) &&
        Objects.equals(this.memberId, entity.memberId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roleId, memberId);
  }

}