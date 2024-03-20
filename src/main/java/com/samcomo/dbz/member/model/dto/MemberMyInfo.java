package com.samcomo.dbz.member.model.dto;

import com.samcomo.dbz.member.model.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberMyInfo {

  private String email;

  private String nickname;

  private String profileImageUrl;

  private String phone;

  public static MemberMyInfo from(Member member) {
    return MemberMyInfo.builder()
        .email(member.getEmail())
        .nickname(member.getNickname())
        .profileImageUrl(member.getProfileImageUrl())
        .phone(member.getPhone())
        .build();
  }
}
