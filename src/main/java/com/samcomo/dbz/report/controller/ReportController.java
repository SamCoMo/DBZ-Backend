package com.samcomo.dbz.report.controller;

import com.samcomo.dbz.report.model.dto.ReportForm;
import com.samcomo.dbz.report.model.dto.ReportResponse;
import com.samcomo.dbz.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
  public ResponseEntity<?> registerReport(
//      Authentication authentication
      @RequestPart ReportForm reportForm,
      @RequestPart(value = "imageList", required = false) List<MultipartFile> imageList
  ){

    //TODO: Authentication에서 Member 정보 가져오기

    // Member 도메인코드가 작성이 안돼서 임시로 진행
    long memberId = 1L;

    ReportResponse reportResponse = reportService.uploadReport(memberId, reportForm, imageList);


    return ResponseEntity.ok(reportResponse);
  }

}
