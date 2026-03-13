package com.taektaek.mymo.repository;

import com.taektaek.mymo.domain.MusicLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicLogRepository extends JpaRepository<MusicLog, Long> {

  List<MusicLog> findByMemberIdOrderByDateDesc(Long memberId);
}
