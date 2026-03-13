package com.taektaek.mymo.service;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.dto.member.MemberResponse;
import com.taektaek.mymo.dto.member.MemberUpdateRequest;
import com.taektaek.mymo.exception.DuplicateMemberException;
import com.taektaek.mymo.exception.ErrorCode;
import com.taektaek.mymo.exception.MemberNotFoundException;
import com.taektaek.mymo.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepository memberRepository;

  public MemberService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  public MemberResponse getMember(Long id) {
    Member member = findMemberById(id);
    return MemberResponse.from(member);
  }

  @Transactional
  public MemberResponse updateMember(Long id, MemberUpdateRequest request) {
    Member member = findMemberById(id);

    memberRepository
        .findByUsername(request.username())
        .filter(existing -> !existing.getId().equals(id))
        .ifPresent(
            existing -> {
              throw new DuplicateMemberException(ErrorCode.DUPLICATE_USERNAME);
            });

    memberRepository
        .findByEmail(request.email())
        .filter(existing -> !existing.getId().equals(id))
        .ifPresent(
            existing -> {
              throw new DuplicateMemberException(ErrorCode.DUPLICATE_EMAIL);
            });

    member.updateProfile(request.username(), request.email());
    return MemberResponse.from(member);
  }

  @Transactional
  public void deleteMember(Long id) {
    Member member = findMemberById(id);
    memberRepository.delete(member);
  }

  private Member findMemberById(Long id) {
    return memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
  }
}
