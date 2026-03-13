package com.taektaek.mymo.repository;

import com.taektaek.mymo.domain.DailyLog;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {

  List<DailyLog> findByMemberIdOrderByDateDesc(Long memberId);

  Optional<DailyLog> findByMemberIdAndDate(Long memberId, LocalDate date);

  boolean existsByMemberIdAndDate(Long memberId, LocalDate date);

  @Query(
      "SELECT d FROM DailyLog d WHERE d.member.id = :memberId"
          + " AND (:startDate IS NULL OR d.date >= :startDate)"
          + " AND (:endDate IS NULL OR d.date <= :endDate)"
          + " AND (:keyword IS NULL"
          + " OR LOWER(d.resolution) LIKE LOWER(CONCAT('%', :keyword, '%'))"
          + " OR LOWER(d.reflection) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  Page<DailyLog> searchByMemberId(
      @Param("memberId") Long memberId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("keyword") String keyword,
      Pageable pageable);
}
