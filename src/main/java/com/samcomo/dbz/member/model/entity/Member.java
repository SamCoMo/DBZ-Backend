package com.samcomo.dbz.member.model.entity;

import com.samcomo.dbz.global.entity.BaseEntity;
import com.samcomo.dbz.member.model.constants.MemberRole;
import com.samcomo.dbz.member.model.constants.MemberStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;

  private String password;

  private String nickname;

  private String profileImageUrl;

  private String phone;

  @Enumerated(EnumType.STRING)
  private MemberRole role;

  @Enumerated(EnumType.STRING)
  private MemberStatus status;

  private String fcmKey;

  @Builder
  public Member(Long id, String email, String password, String nickname, String phone,
      String profileImageUrl, MemberRole role, MemberStatus status, String fcmKey) {

    this.id = id;
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.phone = phone;
    this.profileImageUrl = profileImageUrl;
    this.role = role;
    this.status = status;
    this.fcmKey = fcmKey;
  }

  public void encodePassword(PasswordEncoder passwordEncoder, String rawPassword) {

    this.password = passwordEncoder.encode(rawPassword);
  }
}
