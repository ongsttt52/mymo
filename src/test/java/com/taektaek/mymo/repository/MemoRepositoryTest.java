package com.taektaek.mymo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.domain.Memo;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MemoRepositoryTest {

  @Autowired private MemoRepository memoRepository;

  @Autowired private MemberRepository memberRepository;

  private Member member;

  @BeforeEach
  void setUp() {
    member = memberRepository.save(new Member("testuser", "test@example.com", "password123"));
  }

  @Test
  @DisplayName("회원 ID로 메모를 수정일 내림차순으로 조회한다")
  void findByMemberIdOrderByUpdatedAtDesc() {
    // given
    memoRepository.save(new Memo("첫 번째 메모", member));
    memoRepository.save(new Memo("두 번째 메모", member));
    memoRepository.save(new Memo("세 번째 메모", member));

    // when
    List<Memo> memos = memoRepository.findByMemberIdOrderByUpdatedAtDesc(member.getId());

    // then
    assertThat(memos).hasSize(3);
  }

  @Test
  @DisplayName("다른 회원의 메모는 조회되지 않는다")
  void findByMemberIdOrderByUpdatedAtDesc_otherMember() {
    // given
    Member otherMember =
        memberRepository.save(new Member("other", "other@example.com", "password123"));
    memoRepository.save(new Memo("내 메모", member));
    memoRepository.save(new Memo("다른 사람 메모", otherMember));

    // when
    List<Memo> memos = memoRepository.findByMemberIdOrderByUpdatedAtDesc(member.getId());

    // then
    assertThat(memos).hasSize(1);
    assertThat(memos.get(0).getContent()).isEqualTo("내 메모");
  }
}
