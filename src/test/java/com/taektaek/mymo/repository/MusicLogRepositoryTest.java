package com.taektaek.mymo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.domain.MusicLog;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MusicLogRepositoryTest {

  @Autowired private MusicLogRepository musicLogRepository;

  @Autowired private MemberRepository memberRepository;

  private Member member;

  @BeforeEach
  void setUp() {
    member = memberRepository.save(new Member("testuser", "test@example.com", "password123"));
  }

  @Test
  @DisplayName("회원 ID로 음악 기록을 날짜 내림차순으로 조회한다")
  void findByMemberIdOrderByDateDesc() {
    // given
    musicLogRepository.save(
        new MusicLog("곡1", "가수1", null, null, null, null, LocalDate.of(2026, 3, 1), member));
    musicLogRepository.save(
        new MusicLog("곡2", "가수2", null, null, null, null, LocalDate.of(2026, 3, 3), member));
    musicLogRepository.save(
        new MusicLog("곡3", "가수3", null, null, null, null, LocalDate.of(2026, 3, 2), member));

    // when
    List<MusicLog> musicLogs = musicLogRepository.findByMemberIdOrderByDateDesc(member.getId());

    // then
    assertThat(musicLogs).hasSize(3);
    assertThat(musicLogs.get(0).getDate()).isEqualTo(LocalDate.of(2026, 3, 3));
    assertThat(musicLogs.get(1).getDate()).isEqualTo(LocalDate.of(2026, 3, 2));
    assertThat(musicLogs.get(2).getDate()).isEqualTo(LocalDate.of(2026, 3, 1));
  }

  @Test
  @DisplayName("다른 회원의 음악 기록은 조회되지 않는다")
  void findByMemberIdOrderByDateDesc_otherMember() {
    // given
    Member otherMember =
        memberRepository.save(new Member("other", "other@example.com", "password123"));
    musicLogRepository.save(
        new MusicLog("내 곡", "가수", null, null, null, null, LocalDate.of(2026, 3, 1), member));
    musicLogRepository.save(
        new MusicLog("다른 곡", "가수", null, null, null, null, LocalDate.of(2026, 3, 1), otherMember));

    // when
    List<MusicLog> musicLogs = musicLogRepository.findByMemberIdOrderByDateDesc(member.getId());

    // then
    assertThat(musicLogs).hasSize(1);
    assertThat(musicLogs.get(0).getTitle()).isEqualTo("내 곡");
  }
}
