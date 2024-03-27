package com.samcomo.dbz.member.model.dto;

import com.samcomo.dbz.member.model.constants.LoginType;
import com.samcomo.dbz.member.model.constants.MemberRole;
import com.samcomo.dbz.member.model.dto.oauth2.Oauth2MemberDetails;
import com.samcomo.dbz.member.model.entity.Member;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class MemberDetails implements UserDetails {

  private final Member member;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    Collection<GrantedAuthority> collection = new ArrayList<>();

    collection.add(new GrantedAuthority() {
      @Override
      public String getAuthority() {
        return member.getRole().getKey();
      }
    });

    return collection;
  }

  public static MemberDetails from(Long memberId, MemberRole role) {
    return new MemberDetails(Member.builder()
        .id(memberId)
        .role(role)
        .build());
  }

  public static MemberDetails from(Oauth2MemberDetails details) {
    return new MemberDetails(details.getMember());
  }

  @Override
  public String getPassword() {
    return member.getPassword();
  }

  @Override
  public String getUsername() {
    return member.getEmail();
  }

  public long getId() {
    return member.getId();
  }

  public String getIdAsString() {
    return String.valueOf(member.getId());
  }

  public String getEmail() {
    return member.getEmail();
  }

  public LoginType getLoginType() {
    return member.getLoginType();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
