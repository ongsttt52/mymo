package com.taektaek.mymo.repository;

import com.taektaek.mymo.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("username으로 회원을 조회한다")
    void findByUsername() {
        // given
        Member member = new Member("testuser", "test@example.com", "password123");
        memberRepository.save(member);

        // when
        Optional<Member> found = memberRepository.findByUsername("testuser");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("존재하지 않는 username으로 조회하면 빈 Optional을 반환한다")
    void findByUsername_notFound() {
        // when
        Optional<Member> found = memberRepository.findByUsername("nonexistent");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("username 존재 여부를 확인한다")
    void existsByUsername() {
        // given
        Member member = new Member("testuser", "test@example.com", "password123");
        memberRepository.save(member);

        // when & then
        assertThat(memberRepository.existsByUsername("testuser")).isTrue();
        assertThat(memberRepository.existsByUsername("nonexistent")).isFalse();
    }

    @Test
    @DisplayName("email 존재 여부를 확인한다")
    void existsByEmail() {
        // given
        Member member = new Member("testuser", "test@example.com", "password123");
        memberRepository.save(member);

        // when & then
        assertThat(memberRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(memberRepository.existsByEmail("other@example.com")).isFalse();
    }

    @Test
    @DisplayName("email로 회원을 조회한다")
    void findByEmail() {
        // given
        Member member = new Member("testuser", "test@example.com", "password123");
        memberRepository.save(member);

        // when
        Optional<Member> found = memberRepository.findByEmail("test@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }
}
