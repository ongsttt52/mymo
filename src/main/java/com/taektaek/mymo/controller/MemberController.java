package com.taektaek.mymo.controller;

import com.taektaek.mymo.dto.member.MemberResponse;
import com.taektaek.mymo.dto.member.MemberUpdateRequest;
import com.taektaek.mymo.security.CurrentMemberId;
import com.taektaek.mymo.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원")
@RestController
@RequestMapping("/api/members")
public class MemberController {

  private final MemberService memberService;

  public MemberController(MemberService memberService) {
    this.memberService = memberService;
  }

  @GetMapping("/me")
  public ResponseEntity<MemberResponse> getMyInfo(@CurrentMemberId Long memberId) {
    MemberResponse response = memberService.getMember(memberId);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/me")
  public ResponseEntity<MemberResponse> updateMyInfo(
      @CurrentMemberId Long memberId, @Valid @RequestBody MemberUpdateRequest request) {
    MemberResponse response = memberService.updateMember(memberId, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/me")
  public ResponseEntity<Void> deleteMyAccount(@CurrentMemberId Long memberId) {
    memberService.deleteMember(memberId);
    return ResponseEntity.noContent().build();
  }
}
