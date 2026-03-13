package com.taektaek.mymo.dto.member;

import com.taektaek.mymo.domain.Member;

public record MemberResponse(Long id, String username, String email) {

  public static MemberResponse from(Member member) {
    return new MemberResponse(member.getId(), member.getUsername(), member.getEmail());
  }
}
