package com.taektaek.mymo.repository;

import com.taektaek.mymo.domain.Memo;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemoRepository extends JpaRepository<Memo, Long> {

  List<Memo> findByMemberIdOrderByUpdatedAtDesc(Long memberId);

  @Query(
      "SELECT m FROM Memo m WHERE m.member.id = :memberId"
          + " AND (:keyword IS NULL"
          + " OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  Page<Memo> searchByMemberId(
      @Param("memberId") Long memberId, @Param("keyword") String keyword, Pageable pageable);
}
