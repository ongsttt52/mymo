package com.taektaek.mymo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.taektaek.mymo.domain.DailyLog;
import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.dto.dailylog.DailyLogCreateRequest;
import com.taektaek.mymo.dto.dailylog.DailyLogResponse;
import com.taektaek.mymo.dto.dailylog.DailyLogUpdateRequest;
import com.taektaek.mymo.exception.DailyLogNotFoundException;
import com.taektaek.mymo.exception.DuplicateDailyLogDateException;
import com.taektaek.mymo.exception.MemberNotFoundException;
import com.taektaek.mymo.exception.ResourceAccessDeniedException;
import com.taektaek.mymo.repository.DailyLogRepository;
import com.taektaek.mymo.repository.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DailyLogServiceTest {

  @InjectMocks private DailyLogService dailyLogService;

  @Mock private DailyLogRepository dailyLogRepository;

  @Mock private MemberRepository memberRepository;

  private Member createMember(Long id) {
    Member member = new Member("testuser", "test@example.com", "password123");
    ReflectionTestUtils.setField(member, "id", id);
    return member;
  }

  private DailyLog createDailyLog(
      Long id, LocalDate date, String resolution, String reflection, Member member) {
    DailyLog dailyLog = new DailyLog(date, resolution, reflection, member);
    ReflectionTestUtils.setField(dailyLog, "id", id);
    return dailyLog;
  }

  @Nested
  @DisplayName("일일 기록 생성")
  class CreateDailyLog {

    @Test
    @DisplayName("정상적으로 일일 기록을 생성한다")
    void success() {
      // given
      Member member = createMember(1L);
      LocalDate date = LocalDate.of(2026, 3, 1);
      DailyLogCreateRequest request = new DailyLogCreateRequest(date, "오늘의 다짐", "오늘의 회고");
      DailyLog savedDailyLog = createDailyLog(1L, date, "오늘의 다짐", "오늘의 회고", member);

      given(memberRepository.findById(1L)).willReturn(Optional.of(member));
      given(dailyLogRepository.existsByMemberIdAndDate(1L, date)).willReturn(false);
      given(dailyLogRepository.save(any(DailyLog.class))).willReturn(savedDailyLog);

      // when
      DailyLogResponse response = dailyLogService.createDailyLog(1L, request);

      // then
      assertThat(response.id()).isEqualTo(1L);
      assertThat(response.date()).isEqualTo(date);
      assertThat(response.resolution()).isEqualTo("오늘의 다짐");
      assertThat(response.reflection()).isEqualTo("오늘의 회고");
    }

    @Test
    @DisplayName("존재하지 않는 회원이면 예외를 던진다")
    void memberNotFound() {
      // given
      DailyLogCreateRequest request = new DailyLogCreateRequest(LocalDate.now(), "다짐", "회고");
      given(memberRepository.findById(999L)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> dailyLogService.createDailyLog(999L, request))
          .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("같은 날짜에 기록이 이미 존재하면 예외를 던진다")
    void duplicateDate() {
      // given
      Member member = createMember(1L);
      LocalDate date = LocalDate.of(2026, 3, 1);
      DailyLogCreateRequest request = new DailyLogCreateRequest(date, "다짐", "회고");

      given(memberRepository.findById(1L)).willReturn(Optional.of(member));
      given(dailyLogRepository.existsByMemberIdAndDate(1L, date)).willReturn(true);

      // when & then
      assertThatThrownBy(() -> dailyLogService.createDailyLog(1L, request))
          .isInstanceOf(DuplicateDailyLogDateException.class);
    }
  }

  @Nested
  @DisplayName("일일 기록 조회")
  class GetDailyLog {

    @Test
    @DisplayName("ID로 일일 기록을 조회한다")
    void success() {
      // given
      Member member = createMember(1L);
      LocalDate date = LocalDate.of(2026, 3, 1);
      DailyLog dailyLog = createDailyLog(1L, date, "다짐", "회고", member);
      given(dailyLogRepository.findById(1L)).willReturn(Optional.of(dailyLog));

      // when
      DailyLogResponse response = dailyLogService.getDailyLog(1L, 1L);

      // then
      assertThat(response.id()).isEqualTo(1L);
      assertThat(response.date()).isEqualTo(date);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회하면 예외를 던진다")
    void notFound() {
      // given
      given(dailyLogRepository.findById(999L)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> dailyLogService.getDailyLog(999L, 1L))
          .isInstanceOf(DailyLogNotFoundException.class);
    }

    @Test
    @DisplayName("다른 회원의 기록을 조회하면 예외를 던진다")
    void accessDenied() {
      // given
      Member member = createMember(1L);
      DailyLog dailyLog = createDailyLog(1L, LocalDate.of(2026, 3, 1), "다짐", "회고", member);
      given(dailyLogRepository.findById(1L)).willReturn(Optional.of(dailyLog));

      // when & then
      assertThatThrownBy(() -> dailyLogService.getDailyLog(1L, 2L))
          .isInstanceOf(ResourceAccessDeniedException.class);
    }

    @Test
    @DisplayName("회원별 일일 기록을 조회한다")
    void getByMember() {
      // given
      Member member = createMember(1L);
      List<DailyLog> dailyLogs =
          List.of(
              createDailyLog(1L, LocalDate.of(2026, 3, 2), "다짐2", "회고2", member),
              createDailyLog(2L, LocalDate.of(2026, 3, 1), "다짐1", "회고1", member));
      given(dailyLogRepository.findByMemberIdOrderByDateDesc(1L)).willReturn(dailyLogs);

      // when
      List<DailyLogResponse> responses = dailyLogService.getDailyLogsByMember(1L);

      // then
      assertThat(responses).hasSize(2);
      assertThat(responses.get(0).date()).isEqualTo(LocalDate.of(2026, 3, 2));
      assertThat(responses.get(1).date()).isEqualTo(LocalDate.of(2026, 3, 1));
    }
  }

  @Nested
  @DisplayName("일일 기록 수정")
  class UpdateDailyLog {

    @Test
    @DisplayName("일일 기록을 수정한다")
    void success() {
      // given
      Member member = createMember(1L);
      DailyLog dailyLog = createDailyLog(1L, LocalDate.of(2026, 3, 1), "이전 다짐", "이전 회고", member);
      DailyLogUpdateRequest request = new DailyLogUpdateRequest("새 다짐", "새 회고");

      given(dailyLogRepository.findById(1L)).willReturn(Optional.of(dailyLog));

      // when
      DailyLogResponse response = dailyLogService.updateDailyLog(1L, 1L, request);

      // then
      assertThat(response.resolution()).isEqualTo("새 다짐");
      assertThat(response.reflection()).isEqualTo("새 회고");
    }

    @Test
    @DisplayName("존재하지 않는 기록을 수정하면 예외를 던진다")
    void notFound() {
      // given
      DailyLogUpdateRequest request = new DailyLogUpdateRequest("다짐", "회고");
      given(dailyLogRepository.findById(999L)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> dailyLogService.updateDailyLog(999L, 1L, request))
          .isInstanceOf(DailyLogNotFoundException.class);
    }

    @Test
    @DisplayName("다른 회원의 기록을 수정하면 예외를 던진다")
    void accessDenied() {
      // given
      Member member = createMember(1L);
      DailyLog dailyLog = createDailyLog(1L, LocalDate.of(2026, 3, 1), "다짐", "회고", member);
      DailyLogUpdateRequest request = new DailyLogUpdateRequest("새 다짐", "새 회고");

      given(dailyLogRepository.findById(1L)).willReturn(Optional.of(dailyLog));

      // when & then
      assertThatThrownBy(() -> dailyLogService.updateDailyLog(1L, 2L, request))
          .isInstanceOf(ResourceAccessDeniedException.class);
    }
  }

  @Nested
  @DisplayName("일일 기록 삭제")
  class DeleteDailyLog {

    @Test
    @DisplayName("일일 기록을 삭제한다")
    void success() {
      // given
      Member member = createMember(1L);
      DailyLog dailyLog = createDailyLog(1L, LocalDate.of(2026, 3, 1), "다짐", "회고", member);
      given(dailyLogRepository.findById(1L)).willReturn(Optional.of(dailyLog));

      // when
      dailyLogService.deleteDailyLog(1L, 1L);

      // then
      verify(dailyLogRepository).delete(dailyLog);
    }

    @Test
    @DisplayName("존재하지 않는 기록을 삭제하면 예외를 던진다")
    void notFound() {
      // given
      given(dailyLogRepository.findById(999L)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> dailyLogService.deleteDailyLog(999L, 1L))
          .isInstanceOf(DailyLogNotFoundException.class);
    }

    @Test
    @DisplayName("다른 회원의 기록을 삭제하면 예외를 던진다")
    void accessDenied() {
      // given
      Member member = createMember(1L);
      DailyLog dailyLog = createDailyLog(1L, LocalDate.of(2026, 3, 1), "다짐", "회고", member);
      given(dailyLogRepository.findById(1L)).willReturn(Optional.of(dailyLog));

      // when & then
      assertThatThrownBy(() -> dailyLogService.deleteDailyLog(1L, 2L))
          .isInstanceOf(ResourceAccessDeniedException.class);
    }
  }
}
