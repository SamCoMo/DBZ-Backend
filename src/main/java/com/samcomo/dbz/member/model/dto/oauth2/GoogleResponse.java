package com.samcomo.dbz.member.model.dto.oauth2;

import static com.samcomo.dbz.member.model.constants.LoginType.GOOGLE;

import com.samcomo.dbz.member.model.constants.LoginType;
import java.util.Map;

public class GoogleResponse implements Oauth2Response {

  private final Map<String, Object> attribute;

  public GoogleResponse(Map<String, Object> attribute) {
    this.attribute = attribute;
  }

  @Override
  public LoginType getLoginType() {
    return GOOGLE;
  }

  @Override
  public String getEmail() {
    return String.valueOf(attribute.get("email"));
  }

  @Override
  public String getNickname() {
    return String.valueOf(attribute.get("name")).replaceAll(" ", "");
  }

  @Override
  public String getProfileImageUrl() {
    return String.valueOf(attribute.get("picture"));
  }
}
