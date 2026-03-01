package com.taektaek.mymo.service;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.dto.auth.LoginRequest;
import com.taektaek.mymo.dto.auth.LoginResponse;
import com.taektaek.mymo.dto.member.MemberCreateRequest;
import com.taektaek.mymo.dto.member.MemberResponse;
import com.taektaek.mymo.exception.DuplicateMemberException;
import com.taektaek.mymo.exception.InvalidCredentialsException;
import com.taektaek.mymo.repository.MemberRepository;
import com.taektaek.mymo.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private Member createMember(Long id, String username, String email, String password) {
        Member member = new Member(username, email, password);
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    @Nested
    @DisplayName("회원가입")
    class Signup {

        @Test
        @DisplayName("정상적으로 회원가입한다")
        void success() {
            // given
            MemberCreateRequest request = new MemberCreateRequest("testuser", "test@example.com", "password123");
            Member savedMember = createMember(1L, "testuser", "test@example.com", "encodedPassword");

            given(memberRepository.existsByUsername("testuser")).willReturn(false);
            given(memberRepository.existsByEmail("test@example.com")).willReturn(false);
            given(passwordEncoder.encode("password123")).willReturn("encodedPassword");
            given(memberRepository.save(any(Member.class))).willReturn(savedMember);

            // when
            MemberResponse response = authService.signup(request);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.username()).isEqualTo("testuser");
            assertThat(response.email()).isEqualTo("test@example.com");
            verify(passwordEncoder).encode("password123");
        }

        @Test
        @DisplayName("중복된 username이면 예외를 던진다")
        void duplicateUsername() {
            // given
            MemberCreateRequest request = new MemberCreateRequest("testuser", "test@example.com", "password123");
            given(memberRepository.existsByUsername("testuser")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> authService.signup(request))
                    .isInstanceOf(DuplicateMemberException.class);
        }

        @Test
        @DisplayName("중복된 email이면 예외를 던진다")
        void duplicateEmail() {
            // given
            MemberCreateRequest request = new MemberCreateRequest("testuser", "test@example.com", "password123");
            given(memberRepository.existsByUsername("testuser")).willReturn(false);
            given(memberRepository.existsByEmail("test@example.com")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> authService.signup(request))
                    .isInstanceOf(DuplicateMemberException.class);
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("정상적으로 로그인한다")
        void success() {
            // given
            LoginRequest request = new LoginRequest("test@example.com", "password123");
            Member member = createMember(1L, "testuser", "test@example.com", "encodedPassword");

            given(memberRepository.findByEmail("test@example.com")).willReturn(Optional.of(member));
            given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(true);
            given(jwtTokenProvider.createToken(1L, "test@example.com")).willReturn("jwt-token");

            // when
            LoginResponse response = authService.login(request);

            // then
            assertThat(response.accessToken()).isEqualTo("jwt-token");
            assertThat(response.memberId()).isEqualTo(1L);
            assertThat(response.email()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("존재하지 않는 이메일이면 예외를 던진다")
        void emailNotFound() {
            // given
            LoginRequest request = new LoginRequest("notfound@example.com", "password123");
            given(memberRepository.findByEmail("notfound@example.com")).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(InvalidCredentialsException.class);
        }

        @Test
        @DisplayName("비밀번호가 틀리면 예외를 던진다")
        void wrongPassword() {
            // given
            LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");
            Member member = createMember(1L, "testuser", "test@example.com", "encodedPassword");

            given(memberRepository.findByEmail("test@example.com")).willReturn(Optional.of(member));
            given(passwordEncoder.matches("wrongPassword", "encodedPassword")).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(InvalidCredentialsException.class);
        }
    }
}
