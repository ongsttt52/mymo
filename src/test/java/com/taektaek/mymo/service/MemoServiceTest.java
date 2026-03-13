package com.taektaek.mymo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.domain.Memo;
import com.taektaek.mymo.dto.common.PagedResponse;
import com.taektaek.mymo.dto.memo.MemoCreateRequest;
import com.taektaek.mymo.dto.memo.MemoResponse;
import com.taektaek.mymo.dto.memo.MemoUpdateRequest;
import com.taektaek.mymo.exception.MemberNotFoundException;
import com.taektaek.mymo.exception.MemoNotFoundException;
import com.taektaek.mymo.exception.ResourceAccessDeniedException;
import com.taektaek.mymo.repository.MemberRepository;
import com.taektaek.mymo.repository.MemoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MemoServiceTest {

  @InjectMocks private MemoService memoService;

  @Mock private MemoRepository memoRepository;

  @Mock private MemberRepository memberRepository;

  private Member createMember(Long id) {
    Member member = new Member("testuser", "test@example.com", "password123");
    ReflectionTestUtils.setField(member, "id", id);
    return member;
  }

  private Memo createMemo(Long id, String content, Member member) {
    Memo memo = new Memo(content, member);
    ReflectionTestUtils.setField(memo, "id", id);
    return memo;
  }

  @Nested
  @DisplayName("메모 생성")
  class CreateMemo {

    @Test
    @DisplayName("정상적으로 메모를 생성한다")
    void success() {
      // given
      Member member = createMember(1L);
      MemoCreateRequest request = new MemoCreateRequest("메모 내용");
      Memo savedMemo = createMemo(1L, "메모 내용", member);

      given(memberRepository.findById(1L)).willReturn(Optional.of(member));
      given(memoRepository.save(any(Memo.class))).willReturn(savedMemo);

      // when
      MemoResponse response = memoService.createMemo(1L, request);

      // then
      assertThat(response.id()).isEqualTo(1L);
      assertThat(response.content()).isEqualTo("메모 내용");
    }

    @Test
    @DisplayName("존재하지 않는 회원이면 예외를 던진다")
    void memberNotFound() {
      // given
      MemoCreateRequest request = new MemoCreateRequest("메모 내용");
      given(memberRepository.findById(999L)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> memoService.createMemo(999L, request))
          .isInstanceOf(MemberNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("메모 조회")
  class GetMemo {

    @Test
    @DisplayName("ID로 메모를 조회한다")
    void success() {
      // given
      Member member = createMember(1L);
      Memo memo = createMemo(1L, "메모 내용", member);
      given(memoRepository.findById(1L)).willReturn(Optional.of(memo));

      // when
      MemoResponse response = memoService.getMemo(1L, 1L);

      // then
      assertThat(response.id()).isEqualTo(1L);
      assertThat(response.content()).isEqualTo("메모 내용");
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회하면 예외를 던진다")
    void notFound() {
      // given
      given(memoRepository.findById(999L)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> memoService.getMemo(999L, 1L))
          .isInstanceOf(MemoNotFoundException.class);
    }

    @Test
    @DisplayName("다른 회원의 메모를 조회하면 예외를 던진다")
    void accessDenied() {
      // given
      Member member = createMember(1L);
      Memo memo = createMemo(1L, "메모 내용", member);
      given(memoRepository.findById(1L)).willReturn(Optional.of(memo));

      // when & then
      assertThatThrownBy(() -> memoService.getMemo(1L, 2L))
          .isInstanceOf(ResourceAccessDeniedException.class);
    }

    @Test
    @DisplayName("회원별 메모를 조회한다")
    void getByMember() {
      // given
      Member member = createMember(1L);
      List<Memo> memos = List.of(createMemo(1L, "메모1", member), createMemo(2L, "메모2", member));
      given(memoRepository.findByMemberIdOrderByUpdatedAtDesc(1L)).willReturn(memos);

      // when
      List<MemoResponse> responses = memoService.getMemosByMember(1L);

      // then
      assertThat(responses).hasSize(2);
      assertThat(responses.get(0).content()).isEqualTo("메모1");
      assertThat(responses.get(1).content()).isEqualTo("메모2");
    }
  }

  @Nested
  @DisplayName("메모 검색")
  class SearchMemos {

    @Test
    @DisplayName("검색 조건 없이 페이징 조회한다")
    void searchWithoutConditions() {
      // given
      Member member = createMember(1L);
      List<Memo> memos = List.of(createMemo(1L, "메모 내용", member));
      Page<Memo> page = new PageImpl<>(memos, PageRequest.of(0, 20), 1);
      given(memoRepository.searchByMemberId(eq(1L), eq(null), any(Pageable.class)))
          .willReturn(page);

      // when
      PagedResponse<MemoResponse> response = memoService.searchMemos(1L, null, 0, 20);

      // then
      assertThat(response.content()).hasSize(1);
      assertThat(response.page()).isZero();
      assertThat(response.totalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("빈 키워드는 null로 정규화한다")
    void normalizeBlankKeyword() {
      // given
      Page<Memo> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
      given(memoRepository.searchByMemberId(eq(1L), eq(null), any(Pageable.class)))
          .willReturn(page);

      // when
      PagedResponse<MemoResponse> response = memoService.searchMemos(1L, "   ", 0, 20);

      // then
      assertThat(response.content()).isEmpty();
    }
  }

  @Nested
  @DisplayName("메모 수정")
  class UpdateMemo {

    @Test
    @DisplayName("메모를 수정한다")
    void success() {
      // given
      Member member = createMember(1L);
      Memo memo = createMemo(1L, "이전 내용", member);
      MemoUpdateRequest request = new MemoUpdateRequest("새 내용");

      given(memoRepository.findById(1L)).willReturn(Optional.of(memo));

      // when
      MemoResponse response = memoService.updateMemo(1L, 1L, request);

      // then
      assertThat(response.content()).isEqualTo("새 내용");
    }

    @Test
    @DisplayName("존재하지 않는 메모를 수정하면 예외를 던진다")
    void notFound() {
      // given
      MemoUpdateRequest request = new MemoUpdateRequest("새 내용");
      given(memoRepository.findById(999L)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> memoService.updateMemo(999L, 1L, request))
          .isInstanceOf(MemoNotFoundException.class);
    }

    @Test
    @DisplayName("다른 회원의 메모를 수정하면 예외를 던진다")
    void accessDenied() {
      // given
      Member member = createMember(1L);
      Memo memo = createMemo(1L, "메모 내용", member);
      MemoUpdateRequest request = new MemoUpdateRequest("새 내용");

      given(memoRepository.findById(1L)).willReturn(Optional.of(memo));

      // when & then
      assertThatThrownBy(() -> memoService.updateMemo(1L, 2L, request))
          .isInstanceOf(ResourceAccessDeniedException.class);
    }
  }

  @Nested
  @DisplayName("메모 삭제")
  class DeleteMemo {

    @Test
    @DisplayName("메모를 삭제한다")
    void success() {
      // given
      Member member = createMember(1L);
      Memo memo = createMemo(1L, "메모 내용", member);
      given(memoRepository.findById(1L)).willReturn(Optional.of(memo));

      // when
      memoService.deleteMemo(1L, 1L);

      // then
      verify(memoRepository).delete(memo);
    }

    @Test
    @DisplayName("존재하지 않는 메모를 삭제하면 예외를 던진다")
    void notFound() {
      // given
      given(memoRepository.findById(999L)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> memoService.deleteMemo(999L, 1L))
          .isInstanceOf(MemoNotFoundException.class);
    }

    @Test
    @DisplayName("다른 회원의 메모를 삭제하면 예외를 던진다")
    void accessDenied() {
      // given
      Member member = createMember(1L);
      Memo memo = createMemo(1L, "메모 내용", member);
      given(memoRepository.findById(1L)).willReturn(Optional.of(memo));

      // when & then
      assertThatThrownBy(() -> memoService.deleteMemo(1L, 2L))
          .isInstanceOf(ResourceAccessDeniedException.class);
    }
  }
}
