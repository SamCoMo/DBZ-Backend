package com.samcomo.dbz.member.model.repository;

import com.samcomo.dbz.member.model.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  boolean existsByRefreshTokenAndMemberId(String refreshToken, Long memberId);

  @Transactional
  void deleteByRefreshTokenAndMemberId(String refreshToken, Long memberId);

  void deleteAllByMemberId(Long memberId);
}
