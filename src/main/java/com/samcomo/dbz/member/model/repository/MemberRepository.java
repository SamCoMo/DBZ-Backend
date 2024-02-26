package com.samcomo.dbz.member.model.repository;

import java.lang.reflect.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MemberRepository extends JpaRepository<Member,Long> {

}
