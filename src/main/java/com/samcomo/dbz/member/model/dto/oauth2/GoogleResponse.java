package com.samcomo.dbz.member.model.dto.oauth2;

import com.samcomo.dbz.member.model.constants.LoginType;

import java.util.Map;

import static com.samcomo.dbz.member.model.constants.LoginType.GOOGLE;
import static com.samcomo.dbz.member.model.constants.ParameterKey.EMAIL;

public class GoogleResponse implements Oauth2Response {

  private final static String NICKNAME_KEY = "name";
  private final static String PROFILE_IMAGE_KEY = "picture";

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
    return (String) attribute.get(EMAIL.getKey());
  }

  @Override
  public String getNickname() {
    return String.valueOf(attribute.get(NICKNAME_KEY)).replaceAll(" ", "");
  }

  @Override
  public String getProfileImageUrl() {
    return (String) attribute.get(PROFILE_IMAGE_KEY);
  }
}
