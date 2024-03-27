package com.samcomo.dbz.member.jwt.filter.handler;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samcomo.dbz.global.exception.dto.ErrorResponse;
import com.samcomo.dbz.member.exception.MemberException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

public class FilterMemberExceptionHandler extends OncePerRequestFilter {

  private final static String CHARACTER_ENCODING = "utf-8";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (MemberException e) {
      setErrorResponse(e, response);
    }
  }

  private void setErrorResponse(MemberException e, HttpServletResponse response) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getMessage());

    response.setStatus(e.getErrorCode().getStatus().value());
    response.setContentType(APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(CHARACTER_ENCODING);

    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}
