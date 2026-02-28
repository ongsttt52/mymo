package com.taektaek.mymo.service;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.dto.member.MemberCreateRequest;
import com.taektaek.mymo.dto.member.MemberResponse;
import com.taektaek.mymo.dto.member.MemberUpdateRequest;
import com.taektaek.mymo.exception.DuplicateMemberException;
import com.taektaek.mymo.exception.ErrorCode;
import com.taektaek.mymo.exception.MemberNotFoundException;
import com.taektaek.mymo.repository.MemberRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberResponse createMember(MemberCreateRequest request) {
        validateDuplicateUsername(request.username());
        validateDuplicateEmail(request.email());

        Member member = new Member(request.username(), request.email(), request.password());
        Member savedMember = memberRepository.save(member);
        return MemberResponse.from(savedMember);
    }

    public MemberResponse getMember(Long id) {
        Member member = findMemberById(id);
        return MemberResponse.from(member);
    }

    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(MemberResponse::from)
                .toList();
    }

    @Transactional
    public MemberResponse updateMember(Long id, MemberUpdateRequest request) {
        Member member = findMemberById(id);

        // 자기 자신이 아닌 다른 회원과 중복되는지 검증
        memberRepository.findByUsername(request.username())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateMemberException(ErrorCode.DUPLICATE_USERNAME);
                });

        memberRepository.findByEmail(request.email())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
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
        return memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
    }

    private void validateDuplicateUsername(String username) {
        if (memberRepository.existsByUsername(username)) {
            throw new DuplicateMemberException(ErrorCode.DUPLICATE_USERNAME);
        }
    }

    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateMemberException(ErrorCode.DUPLICATE_EMAIL);
        }
    }
}
