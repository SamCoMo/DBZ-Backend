package com.samcomo.dbz.pin.util;

import static com.samcomo.dbz.global.exception.ErrorCode.ACCESS_DENIED_PIN;
import static com.samcomo.dbz.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.samcomo.dbz.global.exception.ErrorCode.PIN_NOT_FOUND;
import static com.samcomo.dbz.global.exception.ErrorCode.REPORT_NOT_FOUND;

import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.member.model.repository.MemberRepository;
import com.samcomo.dbz.pin.exception.PinException;
import com.samcomo.dbz.pin.model.entity.Pin;
import com.samcomo.dbz.pin.model.repository.PinRepository;
import com.samcomo.dbz.report.exception.ReportException;
import com.samcomo.dbz.report.model.entity.Report;
import com.samcomo.dbz.report.model.repository.ReportRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PinUtils {

  private final MemberRepository memberRepository;
  private final ReportRepository reportRepository;
  private final PinRepository pinRepository;

  // 회원 이메일 검증
  public Member verifyMemberById(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
  }

  // 리포트 아이디 검증
  public Report verifyReportById(Long reportId) {
    return reportRepository.findById(reportId)
        .orElseThrow(() -> new ReportException(REPORT_NOT_FOUND));
  }

  // 핀 검증 + 회원 접근 검증
  public Pin verifyPinByIdAndMemberId(Long pinId, Long memberId){
    // 핀 검증
    Pin pin =  pinRepository.findById(pinId)
        .orElseThrow(() -> new PinException(PIN_NOT_FOUND));

    // 핀 작성 회원 검증
    if(!Objects.equals(pin.getMember().getId(), memberId)){
      throw new PinException(ACCESS_DENIED_PIN);
    }
    return pin;
  }
}