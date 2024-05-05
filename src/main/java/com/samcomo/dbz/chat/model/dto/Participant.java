package com.samcomo.dbz.chat.model.dto;

import com.samcomo.dbz.member.model.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Participant {

  private Long id;
  private String nickname;
  private String profileImageUrl;
  private boolean partner;

  public static Participant from(Member member, boolean partner){
    return Participant.builder()
        .id(member.getId())
        .nickname(member.getNickname())
        .profileImageUrl(member.getProfileImageUrl())
        .partner(partner)
        .build();
  }

}
