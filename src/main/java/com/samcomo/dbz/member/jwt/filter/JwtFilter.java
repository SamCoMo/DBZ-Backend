package com.samcomo.dbz.member.jwt.filter;

public interface JwtFilter {

  boolean isNull(String token);
  void checkExpiration(String token);
  boolean isTokenTypeCorrect(String token);
}
