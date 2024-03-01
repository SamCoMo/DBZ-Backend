package com.samcomo.dbz.member.model.repository;

import com.samcomo.dbz.member.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {

}
