package com.taektaek.mymo.repository;

import com.taektaek.mymo.domain.MusicLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicLogRepository extends JpaRepository<MusicLog, Long> {

    List<MusicLog> findByMemberIdOrderByDateDesc(Long memberId);
}
