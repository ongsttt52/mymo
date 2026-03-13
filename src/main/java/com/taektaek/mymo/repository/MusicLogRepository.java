package com.taektaek.mymo.repository;

import com.taektaek.mymo.domain.MusicLog;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MusicLogRepository extends JpaRepository<MusicLog, Long> {

  List<MusicLog> findByMemberIdOrderByDateDesc(Long memberId);

  @Query(
      "SELECT m FROM MusicLog m WHERE m.member.id = :memberId"
          + " AND (:genre IS NULL OR m.genre = :genre)"
          + " AND (:keyword IS NULL"
          + " OR LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%'))"
          + " OR LOWER(m.artist) LIKE LOWER(CONCAT('%', :keyword, '%'))"
          + " OR LOWER(m.album) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  Page<MusicLog> searchByMemberId(
      @Param("memberId") Long memberId,
      @Param("genre") String genre,
      @Param("keyword") String keyword,
      Pageable pageable);
}
