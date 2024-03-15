package com.samcomo.dbz.member.model.repository;

import com.samcomo.dbz.member.model.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {

  Optional<Member> findByEmail(String email);

  Optional<Member> findByNickname(String nickname);
}
