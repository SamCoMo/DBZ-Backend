package com.samcomo.dbz.member.model.dto;

import com.samcomo.dbz.member.model.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageResponse {

  private String email;
  private String nickname;
  private String profileImageUrl;
  private String phone;

  public static MyPageResponse from(Member member) {
    return MyPageResponse.builder()
        .email(member.getEmail())
        .nickname(member.getNickname())
        .profileImageUrl(member.getProfileImageUrl())
        .phone(member.getPhone())
        .build();
  }
}
