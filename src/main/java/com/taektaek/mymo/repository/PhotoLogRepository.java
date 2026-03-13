package com.taektaek.mymo.repository;

import com.taektaek.mymo.domain.PhotoLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoLogRepository extends JpaRepository<PhotoLog, Long> {

  List<PhotoLog> findByMemberIdOrderByDateDesc(Long memberId);
}
