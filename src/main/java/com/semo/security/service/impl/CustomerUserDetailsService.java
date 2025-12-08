package com.semo.security.service.impl;

import com.semo.entity.Member;
import com.semo.repository.MemberRepository;
import com.semo.security.service.CustomerUserDetails;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

    // Member 엔티티의 역할을 Spring Security의 GrantedAuthority로 변환
    var authorities = member.getMemberRoles().stream()
        .map(memberRole -> new SimpleGrantedAuthority(memberRole.getRole().getName()))
        .collect(Collectors.toSet());

    return new CustomerUserDetails(
        member.getEmail(),
        member.getPassword(),
        member,
        authorities
    );
  }
}
