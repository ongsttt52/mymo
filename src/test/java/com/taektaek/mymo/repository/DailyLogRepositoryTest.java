package com.taektaek.mymo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.taektaek.mymo.domain.DailyLog;
import com.taektaek.mymo.domain.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest
class DailyLogRepositoryTest {

  @Autowired private DailyLogRepository dailyLogRepository;

  @Autowired private MemberRepository memberRepository;

  private Member member;

  @BeforeEach
  void setUp() {
    member = memberRepository.save(new Member("testuser", "test@example.com", "password123"));
  }

  @Test
  @DisplayName("회원 ID로 일일 기록을 날짜 내림차순으로 조회한다")
  void findByMemberIdOrderByDateDesc() {
    // given
    dailyLogRepository.save(new DailyLog(LocalDate.of(2026, 3, 1), "다짐1", "회고1", member));
    dailyLogRepository.save(new DailyLog(LocalDate.of(2026, 3, 3), "다짐3", "회고3", member));
    dailyLogRepository.save(new DailyLog(LocalDate.of(2026, 3, 2), "다짐2", "회고2", member));

    // when
    List<DailyLog> dailyLogs = dailyLogRepository.findByMemberIdOrderByDateDesc(member.getId());

    // then
    assertThat(dailyLogs).hasSize(3);
    assertThat(dailyLogs.get(0).getDate()).isEqualTo(LocalDate.of(2026, 3, 3));
    assertThat(dailyLogs.get(1).getDate()).isEqualTo(LocalDate.of(2026, 3, 2));
    assertThat(dailyLogs.get(2).getDate()).isEqualTo(LocalDate.of(2026, 3, 1));
  }

  @Test
  @DisplayName("회원 ID와 날짜로 일일 기록을 조회한다")
  void findByMemberIdAndDate() {
    // given
    LocalDate date = LocalDate.of(2026, 3, 1);
    dailyLogRepository.save(new DailyLog(date, "오늘의 다짐", "오늘의 회고", member));

    // when
    Optional<DailyLog> found = dailyLogRepository.findByMemberIdAndDate(member.getId(), date);

    // then
    assertThat(found).isPresent();
    assertThat(found.get().getResolution()).isEqualTo("오늘의 다짐");
  }

  @Test
  @DisplayName("회원 ID와 날짜로 기록 존재 여부를 확인한다")
  void existsByMemberIdAndDate() {
    // given
    LocalDate date = LocalDate.of(2026, 3, 1);
    dailyLogRepository.save(new DailyLog(date, "다짐", "회고", member));

    // when & then
    assertThat(dailyLogRepository.existsByMemberIdAndDate(member.getId(), date)).isTrue();
    assertThat(dailyLogRepository.existsByMemberIdAndDate(member.getId(), LocalDate.of(2026, 3, 2)))
        .isFalse();
  }

  @Nested
  @DisplayName("페이징 검색")
  class SearchByMemberId {

    private PageRequest defaultPageRequest;

    @BeforeEach
    void setUp() {
      defaultPageRequest = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "date"));

      dailyLogRepository.save(
          new DailyLog(LocalDate.of(2026, 3, 1), "운동하기", "오늘 운동을 열심히 했다", member));
      dailyLogRepository.save(
          new DailyLog(LocalDate.of(2026, 3, 2), "공부하기", "알고리즘 문제를 풀었다", member));
      dailyLogRepository.save(
          new DailyLog(LocalDate.of(2026, 3, 3), "독서하기", "책을 한 챕터 읽었다", member));
    }

    @Test
    @DisplayName("조건 없이 전체 조회한다")
    void searchWithoutConditions() {
      // when
      Page<DailyLog> result =
          dailyLogRepository.searchByMemberId(member.getId(), null, null, null, defaultPageRequest);

      // then
      assertThat(result.getContent()).hasSize(3);
      assertThat(result.getContent().get(0).getDate()).isEqualTo(LocalDate.of(2026, 3, 3));
    }

    @Test
    @DisplayName("날짜 범위로 필터링한다")
    void searchWithDateRange() {
      // when
      Page<DailyLog> result =
          dailyLogRepository.searchByMemberId(
              member.getId(),
              LocalDate.of(2026, 3, 1),
              LocalDate.of(2026, 3, 2),
              null,
              defaultPageRequest);

      // then
      assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("키워드로 resolution을 검색한다")
    void searchWithKeywordInResolution() {
      // when
      Page<DailyLog> result =
          dailyLogRepository.searchByMemberId(member.getId(), null, null, "운동", defaultPageRequest);

      // then
      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().get(0).getResolution()).isEqualTo("운동하기");
    }

    @Test
    @DisplayName("키워드로 reflection을 검색한다")
    void searchWithKeywordInReflection() {
      // when
      Page<DailyLog> result =
          dailyLogRepository.searchByMemberId(
              member.getId(), null, null, "알고리즘", defaultPageRequest);

      // then
      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().get(0).getReflection()).contains("알고리즘");
    }

    @Test
    @DisplayName("페이징이 정상 동작한다")
    void searchWithPaging() {
      // given
      PageRequest smallPage = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "date"));

      // when
      Page<DailyLog> result =
          dailyLogRepository.searchByMemberId(member.getId(), null, null, null, smallPage);

      // then
      assertThat(result.getContent()).hasSize(2);
      assertThat(result.getTotalElements()).isEqualTo(3);
      assertThat(result.getTotalPages()).isEqualTo(2);
      assertThat(result.isFirst()).isTrue();
      assertThat(result.isLast()).isFalse();
    }
  }
}
