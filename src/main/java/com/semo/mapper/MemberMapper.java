package com.semo.mapper;

import com.semo.dto.login.LoginResponseDTO;
import com.semo.entity.Member;
import com.semo.entity.MemberRole;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring") // Spring 컨테이너에 빈으로 등록되도록 설정
public interface MemberMapper {

  @Mapping(target = "token", ignore = true) // token 필드는 매핑에서 제외 (수동 설정)
  @Mapping(target = "roles", source = "memberRoles", qualifiedByName = "mapRoles") // 역할을 매핑
  LoginResponseDTO toLoginResponseDto(Member member);

  @Named("mapRoles")
  default Set<String> mapRoles(Set<MemberRole> memberRoles) {
    if (memberRoles == null) {
      return null;
    }
    return memberRoles.stream()
        .map(memberRole -> memberRole.getRole().getName())
        .collect(Collectors.toSet());
  }

//  @Named("mapRoles")
//  default List<String> mapRoles(Set<MemberRole> memberRoles) {
//    if (memberRoles == null) {
//      return null;
//    }
//    return memberRoles.stream()
//        .map(MemberRole::getRole)
//        .map(role -> role.getRole().getName())
//        .collect(Collectors.toList());
//  }
}