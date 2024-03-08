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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

  private String profileImageUrl;

  private String phone;

  @Enumerated(EnumType.STRING)
  private MemberRole role;

  @Enumerated(EnumType.STRING)
  private MemberStatus status;

  private String fcmKey;

  public void encodePassword(PasswordEncoder passwordEncoder, String rawPassword) {

    this.password = passwordEncoder.encode(rawPassword);
  }
}
