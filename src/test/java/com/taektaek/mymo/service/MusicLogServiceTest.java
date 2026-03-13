package com.taektaek.mymo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.domain.MusicLog;
import com.taektaek.mymo.dto.musiclog.MusicLogCreateRequest;
import com.taektaek.mymo.dto.musiclog.MusicLogResponse;
import com.taektaek.mymo.dto.musiclog.MusicLogUpdateRequest;
import com.taektaek.mymo.exception.MemberNotFoundException;
import com.taektaek.mymo.exception.MusicLogNotFoundException;
import com.taektaek.mymo.exception.ResourceAccessDeniedException;
import com.taektaek.mymo.repository.MemberRepository;
import com.taektaek.mymo.repository.MusicLogRepository;
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
class MusicLogServiceTest {

  @InjectMocks private MusicLogService musicLogService;

  @Mock private MusicLogRepository musicLogRepository;

  @Mock private MemberRepository memberRepository;

  private Member createMember(Long id) {
    Member member = new Member("testuser", "test@example.com", "password123");
    ReflectionTestUtils.setField(member, "id", id);
    return member;
  }

  private MusicLog createMusicLog(Long id, String title, String artist, Member member) {
    MusicLog musicLog =
        new MusicLog(title, artist, null, null, null, null, LocalDate.of(2026, 3, 1), member);
    ReflectionTestUtils.setField(musicLog, "id", id);
    return musicLog;
  }

  @Nested
  @DisplayName("음악 기록 생성")
  class CreateMusicLog {

    @Test
    @DisplayName("정상적으로 음악 기록을 생성한다")
    void success() {
      // given
      Member member = createMember(1L);
      LocalDate date = LocalDate.of(2026, 3, 1);
      MusicLogCreateRequest request =
          new MusicLogCreateRequest("Spring Day", "BTS", "YNWA", "K-Pop", null, "좋은 곡", date);
      MusicLog savedMusicLog =
          new MusicLog("Spring Day", "BTS", "YNWA", "K-Pop", null, "좋은 곡", date, member);
      ReflectionTestUtils.setField(savedMusicLog, "id", 1L);

      given(memberRepository.findById(1L)).willReturn(Optional.of(member));
      given(musicLogRepository.save(any(MusicLog.class))).willReturn(savedMusicLog);

      // when
      MusicLogResponse response = musicLogService.createMusicLog(1L, request);

      // then
      assertThat(response.id()).isEqualTo(1L);
      assertThat(response.title()).isEqualTo("Spring Day");
      assertThat(response.artist()).isEqualTo("BTS");
    }

    @Test
    @DisplayName("존재하지 않는 회원이면 예외를 던진다")
    void memberNotFound() {
      // given
      MusicLogCreateRequest request =
          new MusicLogCreateRequest("곡", null, null, null, null, null, null);
      given(memberRepository.findById(999L)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> musicLogService.createMusicLog(999L, request))
          .isInstanceOf(MemberNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("음악 기록 조회")
  class GetMusicLog {

    @Test
    @DisplayName("ID로 음악 기록을 조회한다")
    void success() {
      // given
      Member member = createMember(1L);
      MusicLog musicLog = createMusicLog(1L, "Spring Day", "BTS", member);
      given(musicLogRepository.findById(1L)).willReturn(Optional.of(musicLog));

      // when
      MusicLogResponse response = musicLogService.getMusicLog(1L, 1L);

      // then
      assertThat(response.id()).isEqualTo(1L);
      assertThat(response.title()).isEqualTo("Spring Day");
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회하면 예외를 던진다")
    void notFound() {
      // given
      given(musicLogRepository.findById(999L)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> musicLogService.getMusicLog(999L, 1L))
          .isInstanceOf(MusicLogNotFoundException.class);
    }

    @Test
    @DisplayName("다른 회원의 음악 기록을 조회하면 예외를 던진다")
    void accessDenied() {
      // given
      Member member = createMember(1L);
      MusicLog musicLog = createMusicLog(1L, "Spring Day", "BTS", member);
      given(musicLogRepository.findById(1L)).willReturn(Optional.of(musicLog));

      // when & then
      assertThatThrownBy(() -> musicLogService.getMusicLog(1L, 2L))
          .isInstanceOf(ResourceAccessDeniedException.class);
    }

    @Test
    @DisplayName("회원별 음악 기록을 조회한다")
    void getByMember() {
      // given
      Member member = createMember(1L);
      List<MusicLog> musicLogs =
          List.of(createMusicLog(1L, "곡1", "가수1", member), createMusicLog(2L, "곡2", "가수2", member));
      given(musicLogRepository.findByMemberIdOrderByDateDesc(1L)).willReturn(musicLogs);

      // when
      List<MusicLogResponse> responses = musicLogService.getMusicLogsByMember(1L);

      // then
      assertThat(responses).hasSize(2);
      assertThat(responses.get(0).title()).isEqualTo("곡1");
      assertThat(responses.get(1).title()).isEqualTo("곡2");
    }
  }

  @Nested
  @DisplayName("음악 기록 수정")
  class UpdateMusicLog {

    @Test
    @DisplayName("음악 기록을 수정한다")
    void success() {
      // given
      Member member = createMember(1L);
      MusicLog musicLog = createMusicLog(1L, "이전 곡", "이전 가수", member);
      MusicLogUpdateRequest request =
          new MusicLogUpdateRequest(
              "새 곡", "새 가수", "새 앨범", "Pop", null, "감상 수정", LocalDate.of(2026, 3, 2));

      given(musicLogRepository.findById(1L)).willReturn(Optional.of(musicLog));

      // when
      MusicLogResponse response = musicLogService.updateMusicLog(1L, 1L, request);

      // then
      assertThat(response.title()).isEqualTo("새 곡");
      assertThat(response.artist()).isEqualTo("새 가수");
      assertThat(response.album()).isEqualTo("새 앨범");
    }

    @Test
    @DisplayName("존재하지 않는 기록을 수정하면 예외를 던진다")
    void notFound() {
      // given
      MusicLogUpdateRequest request =
          new MusicLogUpdateRequest("곡", null, null, null, null, null, null);
      given(musicLogRepository.findById(999L)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> musicLogService.updateMusicLog(999L, 1L, request))
          .isInstanceOf(MusicLogNotFoundException.class);
    }

    @Test
    @DisplayName("다른 회원의 음악 기록을 수정하면 예외를 던진다")
    void accessDenied() {
      // given
      Member member = createMember(1L);
      MusicLog musicLog = createMusicLog(1L, "곡", "가수", member);
      MusicLogUpdateRequest request =
          new MusicLogUpdateRequest("새 곡", "새 가수", null, null, null, null, null);

      given(musicLogRepository.findById(1L)).willReturn(Optional.of(musicLog));

      // when & then
      assertThatThrownBy(() -> musicLogService.updateMusicLog(1L, 2L, request))
          .isInstanceOf(ResourceAccessDeniedException.class);
    }
  }

  @Nested
  @DisplayName("음악 기록 삭제")
  class DeleteMusicLog {

    @Test
    @DisplayName("음악 기록을 삭제한다")
    void success() {
      // given
      Member member = createMember(1L);
      MusicLog musicLog = createMusicLog(1L, "곡", "가수", member);
      given(musicLogRepository.findById(1L)).willReturn(Optional.of(musicLog));

      // when
      musicLogService.deleteMusicLog(1L, 1L);

      // then
      verify(musicLogRepository).delete(musicLog);
    }

    @Test
    @DisplayName("존재하지 않는 기록을 삭제하면 예외를 던진다")
    void notFound() {
      // given
      given(musicLogRepository.findById(999L)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> musicLogService.deleteMusicLog(999L, 1L))
          .isInstanceOf(MusicLogNotFoundException.class);
    }

    @Test
    @DisplayName("다른 회원의 음악 기록을 삭제하면 예외를 던진다")
    void accessDenied() {
      // given
      Member member = createMember(1L);
      MusicLog musicLog = createMusicLog(1L, "곡", "가수", member);
      given(musicLogRepository.findById(1L)).willReturn(Optional.of(musicLog));

      // when & then
      assertThatThrownBy(() -> musicLogService.deleteMusicLog(1L, 2L))
          .isInstanceOf(ResourceAccessDeniedException.class);
    }
  }
}
