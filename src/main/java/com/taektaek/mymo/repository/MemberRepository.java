package com.taektaek.mymo.repository;

import com.taektaek.mymo.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByUsername(String username);

  Optional<Member> findByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
