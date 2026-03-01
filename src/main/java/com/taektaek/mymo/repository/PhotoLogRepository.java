package com.taektaek.mymo.repository;

import com.taektaek.mymo.domain.PhotoLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoLogRepository extends JpaRepository<PhotoLog, Long> {

    List<PhotoLog> findByMemberIdOrderByDateDesc(Long memberId);
}
