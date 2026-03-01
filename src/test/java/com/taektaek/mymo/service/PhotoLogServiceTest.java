package com.taektaek.mymo.service;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.domain.PhotoLog;
import com.taektaek.mymo.dto.photolog.PhotoLogCreateRequest;
import com.taektaek.mymo.dto.photolog.PhotoLogResponse;
import com.taektaek.mymo.dto.photolog.PhotoLogUpdateRequest;
import com.taektaek.mymo.exception.MemberNotFoundException;
import com.taektaek.mymo.exception.PhotoLogNotFoundException;
import com.taektaek.mymo.repository.MemberRepository;
import com.taektaek.mymo.repository.PhotoLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PhotoLogServiceTest {

    @InjectMocks
    private PhotoLogService photoLogService;

    @Mock
    private PhotoLogRepository photoLogRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member createMember(Long id) {
        Member member = new Member("testuser", "test@example.com", "password123");
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private PhotoLog createPhotoLog(Long id, String imageUrl, String location, Member member) {
        PhotoLog photoLog = new PhotoLog(imageUrl, location, "설명", LocalDate.of(2026, 3, 1), member);
        ReflectionTestUtils.setField(photoLog, "id", id);
        return photoLog;
    }

    @Nested
    @DisplayName("사진 기록 생성")
    class CreatePhotoLog {

        @Test
        @DisplayName("정상적으로 사진 기록을 생성한다")
        void success() {
            // given
            Member member = createMember(1L);
            LocalDate date = LocalDate.of(2026, 3, 1);
            PhotoLogCreateRequest request = new PhotoLogCreateRequest("http://image.url", "성수동", "카페 사진", date);
            PhotoLog savedPhotoLog = new PhotoLog("http://image.url", "성수동", "카페 사진", date, member);
            ReflectionTestUtils.setField(savedPhotoLog, "id", 1L);

            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(photoLogRepository.save(any(PhotoLog.class))).willReturn(savedPhotoLog);

            // when
            PhotoLogResponse response = photoLogService.createPhotoLog(1L, request);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.imageUrl()).isEqualTo("http://image.url");
            assertThat(response.location()).isEqualTo("성수동");
        }

        @Test
        @DisplayName("존재하지 않는 회원이면 예외를 던진다")
        void memberNotFound() {
            // given
            PhotoLogCreateRequest request = new PhotoLogCreateRequest("http://image.url", null, null, null);
            given(memberRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> photoLogService.createPhotoLog(999L, request))
                    .isInstanceOf(MemberNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("사진 기록 조회")
    class GetPhotoLog {

        @Test
        @DisplayName("ID로 사진 기록을 조회한다")
        void success() {
            // given
            Member member = createMember(1L);
            PhotoLog photoLog = createPhotoLog(1L, "http://image.url", "성수동", member);
            given(photoLogRepository.findById(1L)).willReturn(Optional.of(photoLog));

            // when
            PhotoLogResponse response = photoLogService.getPhotoLog(1L);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.imageUrl()).isEqualTo("http://image.url");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 예외를 던진다")
        void notFound() {
            // given
            given(photoLogRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> photoLogService.getPhotoLog(999L))
                    .isInstanceOf(PhotoLogNotFoundException.class);
        }

        @Test
        @DisplayName("회원별 사진 기록을 조회한다")
        void getByMember() {
            // given
            Member member = createMember(1L);
            List<PhotoLog> photoLogs = List.of(
                    createPhotoLog(1L, "url1", "성수동", member),
                    createPhotoLog(2L, "url2", "강남", member)
            );
            given(photoLogRepository.findByMemberIdOrderByDateDesc(1L)).willReturn(photoLogs);

            // when
            List<PhotoLogResponse> responses = photoLogService.getPhotoLogsByMember(1L);

            // then
            assertThat(responses).hasSize(2);
        }
    }

    @Nested
    @DisplayName("사진 기록 수정")
    class UpdatePhotoLog {

        @Test
        @DisplayName("사진 기록을 수정한다")
        void success() {
            // given
            Member member = createMember(1L);
            PhotoLog photoLog = createPhotoLog(1L, "old-url", "이전 장소", member);
            LocalDate newDate = LocalDate.of(2026, 3, 2);
            PhotoLogUpdateRequest request = new PhotoLogUpdateRequest("new-url", "새 장소", "새 설명", newDate);

            given(photoLogRepository.findById(1L)).willReturn(Optional.of(photoLog));

            // when
            PhotoLogResponse response = photoLogService.updatePhotoLog(1L, request);

            // then
            assertThat(response.imageUrl()).isEqualTo("new-url");
            assertThat(response.location()).isEqualTo("새 장소");
            assertThat(response.description()).isEqualTo("새 설명");
            assertThat(response.date()).isEqualTo(newDate);
        }

        @Test
        @DisplayName("존재하지 않는 기록을 수정하면 예외를 던진다")
        void notFound() {
            // given
            PhotoLogUpdateRequest request = new PhotoLogUpdateRequest("url", null, null, null);
            given(photoLogRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> photoLogService.updatePhotoLog(999L, request))
                    .isInstanceOf(PhotoLogNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("사진 기록 삭제")
    class DeletePhotoLog {

        @Test
        @DisplayName("사진 기록을 삭제한다")
        void success() {
            // given
            Member member = createMember(1L);
            PhotoLog photoLog = createPhotoLog(1L, "url", "장소", member);
            given(photoLogRepository.findById(1L)).willReturn(Optional.of(photoLog));

            // when
            photoLogService.deletePhotoLog(1L);

            // then
            verify(photoLogRepository).delete(photoLog);
        }

        @Test
        @DisplayName("존재하지 않는 기록을 삭제하면 예외를 던진다")
        void notFound() {
            // given
            given(photoLogRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> photoLogService.deletePhotoLog(999L))
                    .isInstanceOf(PhotoLogNotFoundException.class);
        }
    }
}
