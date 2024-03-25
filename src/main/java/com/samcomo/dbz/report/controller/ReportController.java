package com.samcomo.dbz.report.controller;


import com.samcomo.dbz.member.model.dto.MemberDetails;
import com.samcomo.dbz.report.model.dto.CustomSlice;
import com.samcomo.dbz.report.model.dto.ReportDto;
import com.samcomo.dbz.report.model.dto.ReportSearchSummaryDto;
import com.samcomo.dbz.report.model.dto.ReportStateDto;
import com.samcomo.dbz.report.model.dto.ReportSummaryDto;
import com.samcomo.dbz.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
@Slf4j
@Tag(name = "게시글 작성 컨트롤러", description = "게시글 관련 API")
public class ReportController {

  private final ReportService reportService;

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
  @Operation(summary = "게시글을 이미지와 함께 작성")
  public ResponseEntity<ReportDto.Response> registerReport(
      @AuthenticationPrincipal MemberDetails details,
      @ModelAttribute ReportDto.Form reportForm
  ) {

    log.info("<<< ReportController - Register Report >>>");

    ReportDto.Response reportResponse =
        reportService.uploadReport(details.getId(), reportForm);

    return ResponseEntity.ok(reportResponse);
  }

  @GetMapping("/{reportId}")
  @Operation(summary = "특정 게시글 정보 가져오기")
  public ResponseEntity<ReportDto.Response> getReport(
      @AuthenticationPrincipal MemberDetails details,
      @PathVariable(value = "reportId") long reportId
  ) {

    ReportDto.Response reportResponse = reportService.getReport(reportId, details.getId());

    return ResponseEntity.ok(reportResponse);
  }

  @GetMapping("/list")
  @Operation(summary = "현재 위치와 인접지역의 게시글 조회")
  public ResponseEntity<CustomSlice<ReportSummaryDto>> getReportList(
      @RequestParam double lastLatitude,
      @RequestParam double lastLongitude,
      @RequestParam double curLatitude,
      @RequestParam double curLongitude,
      @RequestParam boolean showsInProcessOnly,
      Pageable pageable
  ) {

    log.info("<<< ReportController - Get Report List >>>");

    CustomSlice<ReportSummaryDto> result =
        reportService.getReportList(lastLatitude, lastLongitude, curLatitude, curLongitude,
            showsInProcessOnly, pageable);

    return ResponseEntity.ok(result);
  }

  @PutMapping(value = "/{reportId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
      MediaType.APPLICATION_JSON_VALUE})
  @Operation(summary = "게시글 수정")
  public ResponseEntity<ReportDto.Response> updateReport(
      @AuthenticationPrincipal MemberDetails details,
      @PathVariable long reportId,
      @ModelAttribute ReportDto.Form reportForm
  ) {

    ReportDto.Response reportResponse =
        reportService.updateReport(reportId, details.getId(), reportForm);

    return ResponseEntity.ok(reportResponse);
  }

  @DeleteMapping("/{reportId}")
  @Operation(summary = "게시글 삭제")
  public ResponseEntity<ReportStateDto.Response> deleteReport(
      @AuthenticationPrincipal MemberDetails details,
      @PathVariable long reportId
  ) {

    ReportStateDto.Response deleteResponse = reportService.deleteReport(details.getId(), reportId);

    return ResponseEntity.ok(deleteResponse);
  }

  @PutMapping("/{reportId}/complete")
  @Operation(summary = "게시글 완료 처리")
  public ResponseEntity<ReportStateDto.Response> completeProcess(
      @AuthenticationPrincipal MemberDetails details,
      @PathVariable long reportId
  ) {

    ReportStateDto.Response foundResponse =
        reportService.changeStatusToFound(details.getId(), reportId);

    return ResponseEntity.ok(foundResponse);
  }

  @GetMapping("/search")
  @Operation(summary = "게시글 검색")
  public ResponseEntity<CustomSlice<ReportSearchSummaryDto>> searchReport(
      @RequestParam boolean showsInProgressOnly,
      @RequestParam String object,
      Pageable pageable
  ) {

    CustomSlice<ReportSearchSummaryDto> result = reportService.searchReport(object, showsInProgressOnly,
        pageable);

    return ResponseEntity.ok(result);
  }

}
