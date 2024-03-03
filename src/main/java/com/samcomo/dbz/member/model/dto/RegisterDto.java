package com.samcomo.dbz.member.model.dto;

import static com.samcomo.dbz.member.model.constants.MemberRole.MEMBER;
import static com.samcomo.dbz.member.model.constants.MemberStatus.ACTIVE;

import com.samcomo.dbz.member.model.constants.MemberRole;
import com.samcomo.dbz.member.model.constants.MemberStatus;
import com.samcomo.dbz.member.model.entity.Member;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RegisterDto {

  @Getter
  @Builder
  public static class Request {

    private String email;
    private String nickname;
    private String phone;
    private String password;

    public static Member toEntity(Request request) {
      return Member.builder()
          .email(request.getEmail())
          .nickname(request.getNickname())
          .profileImageUrl("defaultImageUrl.jpg") // TODO 기본 이미지 url 정하기
          .phone(request.getPhone())
          .role(MEMBER)
          .status(ACTIVE)
          .build();
    }
  }

  @Getter
  @Builder
  public static class Response {

    private Long memberId;
    private MemberInfo memberInfo;
    private MemberStatus status;
    private MemberRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class MemberInfo {

      private String email;
      private String nickname;
      private String profileImageUrl;
      private String phone;
    }

    public static Response fromEntity(Member member) {

      return Response.builder()
          .memberId(member.getId())
          .memberInfo(
              MemberInfo.builder()
                  .email(member.getEmail())
                  .nickname(member.getNickname())
                  .profileImageUrl(member.getProfileImageUrl())
                  .phone(member.getPhone())
                  .build())
          .status(member.getStatus())
          .role(member.getRole())
          .createdAt(member.getCreatedAt())
          .updatedAt(member.getUpdatedAt())
          .build();
    }
  }
}

