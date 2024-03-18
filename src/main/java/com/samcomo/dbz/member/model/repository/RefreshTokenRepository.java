package com.samcomo.dbz.member.model.repository;

import com.samcomo.dbz.member.model.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  boolean existsByRefreshToken(String refreshToken);

  @Transactional
  void deleteByMemberIdAndRefreshToken(Long memberId, String refreshToken);
}
