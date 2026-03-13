package com.taektaek.mymo.service;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.dto.auth.LoginRequest;
import com.taektaek.mymo.dto.auth.LoginResponse;
import com.taektaek.mymo.dto.member.MemberCreateRequest;
import com.taektaek.mymo.dto.member.MemberResponse;
import com.taektaek.mymo.exception.DuplicateMemberException;
import com.taektaek.mymo.exception.ErrorCode;
import com.taektaek.mymo.exception.InvalidCredentialsException;
import com.taektaek.mymo.repository.MemberRepository;
import com.taektaek.mymo.security.jwt.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  public AuthService(
      MemberRepository memberRepository,
      PasswordEncoder passwordEncoder,
      JwtTokenProvider jwtTokenProvider) {
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Transactional
  public MemberResponse signup(MemberCreateRequest request) {
    validateDuplicateUsername(request.username());
    validateDuplicateEmail(request.email());

    String encodedPassword = passwordEncoder.encode(request.password());
    Member member = new Member(request.username(), request.email(), encodedPassword);
    Member savedMember = memberRepository.save(member);
    return MemberResponse.from(savedMember);
  }

  public LoginResponse login(LoginRequest request) {
    Member member =
        memberRepository.findByEmail(request.email()).orElseThrow(InvalidCredentialsException::new);

    if (!passwordEncoder.matches(request.password(), member.getPassword())) {
      throw new InvalidCredentialsException();
    }

    String token = jwtTokenProvider.createToken(member.getId(), member.getEmail());
    return new LoginResponse(token, member.getId(), member.getEmail());
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
