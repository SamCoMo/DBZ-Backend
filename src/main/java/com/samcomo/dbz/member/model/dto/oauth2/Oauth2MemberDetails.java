package com.samcomo.dbz.member.model.dto.oauth2;

import com.samcomo.dbz.member.model.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class Oauth2MemberDetails implements OAuth2User {

  private final Member member;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> collection = new ArrayList<>();
    collection.add(new GrantedAuthority() {
      @Override
      public String getAuthority() {
        return String.valueOf(member.getRole());
      }
    });
    return collection;
  }

  @Override
  public String getName() {
    return member.getNickname();
  }


  public Member getMember() {
    return this.member;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return null; // 사용 안 함.
  }
}
