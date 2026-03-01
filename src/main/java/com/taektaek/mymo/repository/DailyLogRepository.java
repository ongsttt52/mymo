package com.taektaek.mymo.repository;

import com.taektaek.mymo.domain.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {

    List<DailyLog> findByMemberIdOrderByDateDesc(Long memberId);

    Optional<DailyLog> findByMemberIdAndDate(Long memberId, LocalDate date);

    boolean existsByMemberIdAndDate(Long memberId, LocalDate date);
}
