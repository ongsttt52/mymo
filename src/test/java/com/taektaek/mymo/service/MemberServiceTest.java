package com.taektaek.mymo.service;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.dto.member.MemberCreateRequest;
import com.taektaek.mymo.dto.member.MemberResponse;
import com.taektaek.mymo.dto.member.MemberUpdateRequest;
import com.taektaek.mymo.exception.DuplicateMemberException;
import com.taektaek.mymo.exception.MemberNotFoundException;
import com.taektaek.mymo.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    private Member createMember(Long id, String username, String email) {
        Member member = new Member(username, email, "password123");
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    @Nested
    @DisplayName("회원 생성")
    class CreateMember {

        @Test
        @DisplayName("정상적으로 회원을 생성한다")
        void success() {
            // given
            MemberCreateRequest request = new MemberCreateRequest("testuser", "test@example.com", "password123");
            Member savedMember = createMember(1L, "testuser", "test@example.com");

            given(memberRepository.existsByUsername("testuser")).willReturn(false);
            given(memberRepository.existsByEmail("test@example.com")).willReturn(false);
            given(memberRepository.save(any(Member.class))).willReturn(savedMember);

            // when
            MemberResponse response = memberService.createMember(request);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.username()).isEqualTo("testuser");
            assertThat(response.email()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("중복된 username이면 예외를 던진다")
        void duplicateUsername() {
            // given
            MemberCreateRequest request = new MemberCreateRequest("testuser", "test@example.com", "password123");
            given(memberRepository.existsByUsername("testuser")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> memberService.createMember(request))
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
            assertThatThrownBy(() -> memberService.createMember(request))
                    .isInstanceOf(DuplicateMemberException.class);
        }
    }

    @Nested
    @DisplayName("회원 조회")
    class GetMember {

        @Test
        @DisplayName("ID로 회원을 조회한다")
        void success() {
            // given
            Member member = createMember(1L, "testuser", "test@example.com");
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));

            // when
            MemberResponse response = memberService.getMember(1L);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.username()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 예외를 던진다")
        void notFound() {
            // given
            given(memberRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.getMember(999L))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        @DisplayName("전체 회원을 조회한다")
        void getAllMembers() {
            // given
            List<Member> members = List.of(
                    createMember(1L, "user1", "user1@example.com"),
                    createMember(2L, "user2", "user2@example.com")
            );
            given(memberRepository.findAll()).willReturn(members);

            // when
            List<MemberResponse> responses = memberService.getAllMembers();

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).username()).isEqualTo("user1");
            assertThat(responses.get(1).username()).isEqualTo("user2");
        }
    }

    @Nested
    @DisplayName("회원 수정")
    class UpdateMember {

        @Test
        @DisplayName("회원 정보를 수정한다")
        void success() {
            // given
            Member member = createMember(1L, "oldname", "old@example.com");
            MemberUpdateRequest request = new MemberUpdateRequest("newname", "new@example.com");

            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(memberRepository.findByUsername("newname")).willReturn(Optional.empty());
            given(memberRepository.findByEmail("new@example.com")).willReturn(Optional.empty());

            // when
            MemberResponse response = memberService.updateMember(1L, request);

            // then
            assertThat(response.username()).isEqualTo("newname");
            assertThat(response.email()).isEqualTo("new@example.com");
        }

        @Test
        @DisplayName("자기 자신의 username으로 수정하면 정상 처리된다")
        void sameUsername() {
            // given
            Member member = createMember(1L, "testuser", "test@example.com");
            MemberUpdateRequest request = new MemberUpdateRequest("testuser", "new@example.com");

            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(memberRepository.findByUsername("testuser")).willReturn(Optional.of(member));
            given(memberRepository.findByEmail("new@example.com")).willReturn(Optional.empty());

            // when
            MemberResponse response = memberService.updateMember(1L, request);

            // then
            assertThat(response.username()).isEqualTo("testuser");
            assertThat(response.email()).isEqualTo("new@example.com");
        }

        @Test
        @DisplayName("다른 회원의 username과 중복되면 예외를 던진다")
        void duplicateUsername() {
            // given
            Member member = createMember(1L, "user1", "user1@example.com");
            Member otherMember = createMember(2L, "user2", "user2@example.com");
            MemberUpdateRequest request = new MemberUpdateRequest("user2", "user1@example.com");

            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(memberRepository.findByUsername("user2")).willReturn(Optional.of(otherMember));

            // when & then
            assertThatThrownBy(() -> memberService.updateMember(1L, request))
                    .isInstanceOf(DuplicateMemberException.class);
        }

        @Test
        @DisplayName("존재하지 않는 회원을 수정하면 예외를 던진다")
        void notFound() {
            // given
            MemberUpdateRequest request = new MemberUpdateRequest("newname", "new@example.com");
            given(memberRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.updateMember(999L, request))
                    .isInstanceOf(MemberNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("회원 삭제")
    class DeleteMember {

        @Test
        @DisplayName("회원을 삭제한다")
        void success() {
            // given
            Member member = createMember(1L, "testuser", "test@example.com");
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));

            // when
            memberService.deleteMember(1L);

            // then
            verify(memberRepository).delete(member);
        }

        @Test
        @DisplayName("존재하지 않는 회원을 삭제하면 예외를 던진다")
        void notFound() {
            // given
            given(memberRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.deleteMember(999L))
                    .isInstanceOf(MemberNotFoundException.class);
        }
    }
}
