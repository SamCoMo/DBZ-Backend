package com.samcomo.dbz.member.model.entity;

import static com.samcomo.dbz.member.model.constants.LoginType.DEFAULT;
import static com.samcomo.dbz.member.model.constants.MemberRole.MEMBER;
import static com.samcomo.dbz.member.model.constants.MemberStatus.ACTIVE;

import com.samcomo.dbz.global.entity.BaseEntity;
import com.samcomo.dbz.member.model.constants.LoginType;
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
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@ToString(exclude = "password")
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

  private String phone;

  @Enumerated(EnumType.STRING)
  private MemberRole role;

  @Enumerated(EnumType.STRING)
  private MemberStatus status;

  @Setter
  private String profileImageUrl;
  @Setter
  private String fcmToken;
  @Setter
  private String address;
  @Setter
  private Double latitude;
  @Setter
  private Double longitude;
  @Setter
  private LoginType loginType;

  public void encodePassword(PasswordEncoder passwordEncoder, String rawPassword) {
    this.password = passwordEncoder.encode(rawPassword);
  }

  public static Member from(RegisterRequest request) {
    return Member.builder()
        .email(request.getEmail())
        .nickname(request.getNickname())
        .phone(request.getPhone())
        .role(MEMBER)
        .status(ACTIVE)
        .address(request.getAddress())
        .latitude(request.getLatitude())
        .longitude(request.getLongitude())
        .loginType(DEFAULT)
        .build();
  }

  public void updateEmailAndNickname(String email, String nickname) {
    this.email = email;
    this.nickname = nickname;
  }
}
