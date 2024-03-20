package com.samcomo.dbz.member.model.dto;

import com.samcomo.dbz.member.model.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyInfo {

  private String email;

  private String nickname;

  private String profileImageUrl;

  private String phone;

  public static MyInfo from(Member member) {
    return MyInfo.builder()
        .email(member.getEmail())
        .nickname(member.getNickname())
        .profileImageUrl(member.getProfileImageUrl())
        .phone(member.getPhone())
        .build();
  }
}
