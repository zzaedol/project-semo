package com.semo.security.service;

import com.semo.entity.Member;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class CustomerUserDetails extends User {

  private final String email;
  private final Member member;

  public CustomerUserDetails(String email, String password, Member member, Collection<? extends GrantedAuthority> authorities) {
    super(email, password, authorities);
    this.email = email;
    this.member = member;
  }

  public Long getMemberId() {
    return member.getId();
  }
}
