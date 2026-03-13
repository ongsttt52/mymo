package com.taektaek.mymo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.domain.PhotoLog;
import java.time.LocalDate;
import java.util.List;
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
class PhotoLogRepositoryTest {

  @Autowired private PhotoLogRepository photoLogRepository;

  @Autowired private MemberRepository memberRepository;

  private Member member;

  @BeforeEach
  void setUp() {
    member = memberRepository.save(new Member("testuser", "test@example.com", "password123"));
  }

  @Test
  @DisplayName("회원 ID로 사진 기록을 날짜 내림차순으로 조회한다")
  void findByMemberIdOrderByDateDesc() {
    // given
    photoLogRepository.save(new PhotoLog("url1", "성수동", "설명1", LocalDate.of(2026, 3, 1), member));
    photoLogRepository.save(new PhotoLog("url2", "강남", "설명2", LocalDate.of(2026, 3, 3), member));
    photoLogRepository.save(new PhotoLog("url3", "홍대", "설명3", LocalDate.of(2026, 3, 2), member));

    // when
    List<PhotoLog> photoLogs = photoLogRepository.findByMemberIdOrderByDateDesc(member.getId());

    // then
    assertThat(photoLogs).hasSize(3);
    assertThat(photoLogs.get(0).getDate()).isEqualTo(LocalDate.of(2026, 3, 3));
    assertThat(photoLogs.get(1).getDate()).isEqualTo(LocalDate.of(2026, 3, 2));
    assertThat(photoLogs.get(2).getDate()).isEqualTo(LocalDate.of(2026, 3, 1));
  }

  @Test
  @DisplayName("다른 회원의 사진 기록은 조회되지 않는다")
  void findByMemberIdOrderByDateDesc_otherMember() {
    // given
    Member otherMember =
        memberRepository.save(new Member("other", "other@example.com", "password123"));
    photoLogRepository.save(new PhotoLog("url1", "성수동", "내 사진", LocalDate.of(2026, 3, 1), member));
    photoLogRepository.save(
        new PhotoLog("url2", "강남", "다른 사진", LocalDate.of(2026, 3, 1), otherMember));

    // when
    List<PhotoLog> photoLogs = photoLogRepository.findByMemberIdOrderByDateDesc(member.getId());

    // then
    assertThat(photoLogs).hasSize(1);
    assertThat(photoLogs.get(0).getDescription()).isEqualTo("내 사진");
  }

  @Nested
  @DisplayName("페이징 검색")
  class SearchByMemberId {

    private PageRequest defaultPageRequest;

    @BeforeEach
    void setUp() {
      defaultPageRequest = PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "date"));

      photoLogRepository.save(
          new PhotoLog("url1", "성수동 카페", "라떼 사진", LocalDate.of(2026, 3, 1), member));
      photoLogRepository.save(
          new PhotoLog("url2", "강남역", "거리 풍경", LocalDate.of(2026, 3, 2), member));
      photoLogRepository.save(
          new PhotoLog("url3", "홍대 거리", "벽화 사진", LocalDate.of(2026, 3, 3), member));
    }

    @Test
    @DisplayName("조건 없이 전체 조회한다")
    void searchWithoutConditions() {
      // when
      Page<PhotoLog> result =
          photoLogRepository.searchByMemberId(member.getId(), null, null, null, defaultPageRequest);

      // then
      assertThat(result.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("날짜 범위로 필터링한다")
    void searchWithDateRange() {
      // when
      Page<PhotoLog> result =
          photoLogRepository.searchByMemberId(
              member.getId(),
              LocalDate.of(2026, 3, 1),
              LocalDate.of(2026, 3, 2),
              null,
              defaultPageRequest);

      // then
      assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("키워드로 location을 검색한다")
    void searchWithKeywordInLocation() {
      // when
      Page<PhotoLog> result =
          photoLogRepository.searchByMemberId(
              member.getId(), null, null, "성수동", defaultPageRequest);

      // then
      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().get(0).getLocation()).contains("성수동");
    }

    @Test
    @DisplayName("키워드로 description을 검색한다")
    void searchWithKeywordInDescription() {
      // when
      Page<PhotoLog> result =
          photoLogRepository.searchByMemberId(member.getId(), null, null, "벽화", defaultPageRequest);

      // then
      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().get(0).getDescription()).contains("벽화");
    }
  }
}
