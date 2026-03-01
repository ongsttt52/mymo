package com.taektaek.mymo.repository;

import com.taektaek.mymo.domain.DailyLog;
import com.taektaek.mymo.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DailyLogRepositoryTest {

    @Autowired
    private DailyLogRepository dailyLogRepository;

    @Autowired
    private MemberRepository memberRepository;

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
        assertThat(dailyLogRepository.existsByMemberIdAndDate(member.getId(), LocalDate.of(2026, 3, 2))).isFalse();
    }
}
