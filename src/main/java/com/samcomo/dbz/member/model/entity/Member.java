package com.samcomo.dbz.member.model.entity;

import static com.samcomo.dbz.member.model.constants.MemberRole.MEMBER;
import static com.samcomo.dbz.member.model.constants.MemberStatus.ACTIVE;

import com.samcomo.dbz.global.entity.BaseEntity;
import com.samcomo.dbz.member.model.constants.MemberRole;
import com.samcomo.dbz.member.model.constants.MemberStatus;
import com.samcomo.dbz.member.model.dto.RegisterRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;

  private String password;

  private String nickname;

  @Setter
  private String profileImageUrl;

  private String phone;

  @Enumerated(EnumType.STRING)
  private MemberRole role;

  @Enumerated(EnumType.STRING)
  private MemberStatus status;

  @Setter
  private String fcmToken;

  @Setter
  private String address;
  @Setter
  private Double latitude;
  @Setter
  private Double longitude;

  public void encodePassword(PasswordEncoder passwordEncoder, String rawPassword) {
    this.password = passwordEncoder.encode(rawPassword);
  }

  public static Member from(RegisterRequest request) {
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
