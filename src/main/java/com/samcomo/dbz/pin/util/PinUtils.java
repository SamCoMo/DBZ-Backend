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
import com.samcomo.dbz.pin.model.entity.PinImage;
import com.samcomo.dbz.pin.model.repository.PinImageRepository;
import com.samcomo.dbz.pin.model.repository.PinRepository;
import com.samcomo.dbz.report.exception.ReportException;
import com.samcomo.dbz.report.model.entity.Report;
import com.samcomo.dbz.report.model.repository.ReportRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PinUtils {
  private final MemberRepository memberRepository;
  private final ReportRepository reportRepository;
  private final PinRepository pinRepository;
  private final PinImageRepository pinImageRepository;

  // 회원 이메일 검증
  public Member verifyMemberById(Long memberId){
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
  }

  // 리포트 아이디 검증
  public Report verifyReportById(Long reportId){
    return reportRepository.findById(reportId)
        .orElseThrow(()-> new ReportException(REPORT_NOT_FOUND));
  }

  // 핀 검증
  public Pin verifyPinById(Long pinId){
    return pinRepository.findById(pinId)
        .orElseThrow(() -> new PinException(PIN_NOT_FOUND));
  }

  // 핀 검증 + 업데이트 권한 회원 검증
  public Pin verifyUpdateMemberByPinId(Long memberId, Long pinId){
    // 핀 검증
    Pin pin = verifyPinById(pinId);

    // 핀 작성자만 pin 수정 가능
    if(!(pin.getMember().getId().equals(memberId))){
      throw new PinException(ACCESS_DENIED_PIN);
    }

    return pin;
  }

  // 핀 검증 + 삭제 권한 회원 검증
  public Pin verifyDeleteMemberByPinId (Long memberId, Long pinId){
    // 핀 검증
    Pin pin = verifyPinById(pinId);

    // 리포트 검증
    Report report = verifyReportById(pin.getReport().getId());

    // report 생성회원 or pin 생성회원만 삭제 가능
    if(!(report.getMember().getId().equals(memberId) ||
        pin.getMember().getId().equals(memberId))){
      throw new PinException(ACCESS_DENIED_PIN);
    }
    return pin;
  }

  // 핀 사진 가져오기
  public List<PinImage> getPinImageListByPinId(Long pinId){
    return pinImageRepository.findAllByPin_PinId(pinId);
  }
}