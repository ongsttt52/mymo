package com.taektaek.mymo.security.jwt;

import com.taektaek.mymo.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private static final String SECRET = "mymo-jwt-secret-key-for-test-only-must-be-at-least-256-bits-long";
    private static final long EXPIRATION = 86400000L; // 24시간

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties(SECRET, EXPIRATION);
        jwtTokenProvider = new JwtTokenProvider(jwtProperties);
    }

    @Nested
    @DisplayName("토큰 생성")
    class CreateToken {

        @Test
        @DisplayName("정상적으로 토큰을 생성한다")
        void success() {
            // when
            String token = jwtTokenProvider.createToken(1L, "test@example.com");

            // then
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("토큰 파싱")
    class ParseToken {

        @Test
        @DisplayName("토큰에서 memberId를 추출한다")
        void getMemberId() {
            // given
            String token = jwtTokenProvider.createToken(1L, "test@example.com");

            // when
            Long memberId = jwtTokenProvider.getMemberId(token);

            // then
            assertThat(memberId).isEqualTo(1L);
        }

        @Test
        @DisplayName("토큰에서 email을 추출한다")
        void getEmail() {
            // given
            String token = jwtTokenProvider.createToken(1L, "test@example.com");

            // when
            String email = jwtTokenProvider.getEmail(token);

            // then
            assertThat(email).isEqualTo("test@example.com");
        }
    }

    @Nested
    @DisplayName("토큰 검증")
    class ValidateToken {

        @Test
        @DisplayName("유효한 토큰을 검증한다")
        void validToken() {
            // given
            String token = jwtTokenProvider.createToken(1L, "test@example.com");

            // when & then
            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        }

        @Test
        @DisplayName("변조된 토큰은 검증에 실패한다")
        void tamperedToken() {
            // given
            String token = jwtTokenProvider.createToken(1L, "test@example.com");
            String tamperedToken = token + "tampered";

            // when & then
            assertThat(jwtTokenProvider.validateToken(tamperedToken)).isFalse();
        }

        @Test
        @DisplayName("만료된 토큰은 검증에 실패한다")
        void expiredToken() {
            // given
            JwtProperties expiredProperties = new JwtProperties(SECRET, 0L);
            JwtTokenProvider expiredProvider = new JwtTokenProvider(expiredProperties);
            String token = expiredProvider.createToken(1L, "test@example.com");

            // when & then
            assertThat(expiredProvider.validateToken(token)).isFalse();
        }

        @Test
        @DisplayName("null 토큰은 검증에 실패한다")
        void nullToken() {
            // when & then
            assertThat(jwtTokenProvider.validateToken(null)).isFalse();
        }

        @Test
        @DisplayName("빈 문자열 토큰은 검증에 실패한다")
        void emptyToken() {
            // when & then
            assertThat(jwtTokenProvider.validateToken("")).isFalse();
        }

        @Test
        @DisplayName("다른 키로 서명된 토큰은 검증에 실패한다")
        void differentKeyToken() {
            // given
            JwtProperties otherProperties = new JwtProperties(
                    "other-jwt-secret-key-for-test-only-must-be-at-least-256-bits-long-different", EXPIRATION);
            JwtTokenProvider otherProvider = new JwtTokenProvider(otherProperties);
            String token = otherProvider.createToken(1L, "test@example.com");

            // when & then
            assertThat(jwtTokenProvider.validateToken(token)).isFalse();
        }
    }
}
