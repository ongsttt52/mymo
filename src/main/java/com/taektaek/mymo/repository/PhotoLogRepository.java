package com.taektaek.mymo.repository;

import com.taektaek.mymo.domain.PhotoLog;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PhotoLogRepository extends JpaRepository<PhotoLog, Long> {

  List<PhotoLog> findByMemberIdOrderByDateDesc(Long memberId);

  @Query(
      "SELECT p FROM PhotoLog p WHERE p.member.id = :memberId"
          + " AND (:startDate IS NULL OR p.date >= :startDate)"
          + " AND (:endDate IS NULL OR p.date <= :endDate)"
          + " AND (:keyword IS NULL"
          + " OR LOWER(p.location) LIKE LOWER(CONCAT('%', :keyword, '%'))"
          + " OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  Page<PhotoLog> searchByMemberId(
      @Param("memberId") Long memberId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("keyword") String keyword,
      Pageable pageable);
}
