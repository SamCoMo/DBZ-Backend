package com.samcomo.dbz.report.model.dto;

import com.samcomo.dbz.member.model.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class WriterProfile {

  private String nickname;
  private String profileImageUrl;
  private Long id;

  public static WriterProfile from(Member member){
    return WriterProfile.builder()
        .id(member.getId())
        .nickname(member.getNickname())
        .profileImageUrl(member.getProfileImageUrl())
        .build();
  }
}
