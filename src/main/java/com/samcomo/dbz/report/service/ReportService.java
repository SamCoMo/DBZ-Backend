package com.samcomo.dbz.report.service;

import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.report.model.dto.CustomSlice;
import com.samcomo.dbz.report.model.dto.ReportDto;
import com.samcomo.dbz.report.model.dto.ReportStateDto.Response;
import com.samcomo.dbz.report.model.dto.ReportSummaryDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ReportService {

  ReportDto.Response uploadReport(Member member, ReportDto.Form reportForm,
      List<MultipartFile> imageList);

  ReportDto.Response getReport(long reportId, Member member);
  CustomSlice<ReportSummaryDto> getReportList(
      double lastLatitude, double lastLongitude, double curLatitude, double curLongitude, boolean showsInProcessOnly, Pageable pageable);

  ReportDto.Response updateReport(long reportId, ReportDto.Form reportForm,
      List<MultipartFile> imageList, Member member);

  Response deleteReport(Member member, long reportId);

  Response changeStatusToFound(Member member, long reportId);

  CustomSlice<ReportSummaryDto> searchReport(String object, boolean showsInProgressOnly, Pageable pageable);
}
