package com.samcomo.dbz.report.controller;

import com.samcomo.dbz.report.model.dto.CustomSlice;
import com.samcomo.dbz.report.model.dto.ReportDto;
import com.samcomo.dbz.report.model.dto.ReportList;
import com.samcomo.dbz.report.model.dto.ReportStateDto;
import com.samcomo.dbz.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
@Tag(name = "게시글 작성 컨트롤러", description = "게시글 관련 API")
public class ReportController {

  private final ReportService reportService;

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
  @Operation(summary = "게시글을 이미지와 함께 작성")
  public ResponseEntity<ReportDto.Response> registerReport(
//      Authentication authentication
      @RequestPart ReportDto.Form reportForm,
      @RequestPart(value = "imageList", required = false) List<MultipartFile> imageList
  ) {

    //TODO: Authentication에서 Member 정보 가져오기

    // Member 도메인코드가 작성이 안돼서 임시로 진행
    long memberId = 1L;

    ReportDto.Response reportResponse = reportService.uploadReport(memberId, reportForm, imageList);

    return ResponseEntity.ok(reportResponse);
  }

  @GetMapping("/{reportId}")
  @Operation(summary = "특정 게시글 정보 가져오기")
  public ResponseEntity<ReportDto.Response> getReport(
      @PathVariable(value = "reportId") long reportId
  ) {

    ReportDto.Response reportResponse = reportService.getReport(reportId);

    return ResponseEntity.ok(reportResponse);
  }

  @GetMapping("/list")
  @Operation(summary = "현재 위치와 인접지역의 게시글 조회")
  public ResponseEntity<CustomSlice<ReportList>> getReportList(
      @RequestParam double lastLatitude,
      @RequestParam double lastLongitude,
      @RequestParam double curLatitude,
      @RequestParam double curLongitude,
      @RequestParam boolean showsInProcessOnly,
      Pageable pageable
  ) {

    CustomSlice<ReportList> result =
        reportService.getReportList(lastLongitude, lastLatitude, curLatitude, curLongitude, showsInProcessOnly, pageable);

    return ResponseEntity.ok(result);
  }

  @PutMapping(value = "/{reportId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
      MediaType.APPLICATION_JSON_VALUE})
  @Operation(summary = "게시글 수정")
  public ResponseEntity<ReportDto.Response> updateReport(
//      Authentication authentication
      @PathVariable long reportId,
      @RequestPart ReportDto.Form reportForm,
      @RequestPart(value = "imageList", required = false) List<MultipartFile> imageList
  ) {

    //TODO: 이미지 수정에대한 처리 리팩토링 필요

    //TODO: 유저 정보 가져오기 >> Auth 파트 구현 끝나면 수정 필요
    long userId = 1L;

    ReportDto.Response reportResponse = reportService.updateReport(reportId, reportForm, imageList,
        userId);

    return ResponseEntity.ok(reportResponse);
  }

  @DeleteMapping("/{reportId}")
  @Operation(summary = "게시글 삭제")
  public ResponseEntity<ReportStateDto.Response> deleteReport(
//      Authentication authentication,
      @PathVariable long reportId
  ) {

    //TODO: 유저 정보 가져오기 >> Auth 파트 구현 끝나면 수정 필요
    long userId = 1L;

    ReportStateDto.Response deleteResponse = reportService.deleteReport(userId, reportId);

    return ResponseEntity.ok(deleteResponse);
  }

  @PutMapping("/{reportId}/complete")
  @Operation(summary = "게시글 완료 처리")
  public ResponseEntity<ReportStateDto.Response> completeProcess(
      //      Authentication authentication,
      @PathVariable long reportId
  ) {

    //TODO: 유저 정보 가져오기 >> Auth 파트 구현 끝나면 수정 필요
    long userId = 1L;

    ReportStateDto.Response foundResponse = reportService.changeStatusToFound(userId, reportId);
    return ResponseEntity.ok(foundResponse);
  }

  @GetMapping("/search")
  @Operation(summary = "게시글 검색")
  public ResponseEntity<CustomSlice<ReportList>> searchReport(
      @RequestParam boolean showsInProgressOnly,
      @RequestParam String object,
      Pageable pageable
  ){

    CustomSlice<ReportList> result = reportService.searchReport(object, showsInProgressOnly, pageable);

    return ResponseEntity.ok(result);
  }

}
