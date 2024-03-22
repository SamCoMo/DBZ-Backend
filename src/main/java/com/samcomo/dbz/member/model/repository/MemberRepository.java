package com.samcomo.dbz.member.model.repository;

import com.samcomo.dbz.member.model.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member,Long> {

  Optional<Member> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);

  @Query(value = "select m "
      + "from Member m "
      + "where ST_Distance_Sphere(Point(m.longitude, m.latitude), Point(:longitude, :latitude)) <= :distance "
      + "and m.status = 'INACTIVE'")
  List<Member> findAllInActive(@Param("latitude") double latitude, @Param("longitude") double longitude,
      @Param("distance") Integer distance);
}
