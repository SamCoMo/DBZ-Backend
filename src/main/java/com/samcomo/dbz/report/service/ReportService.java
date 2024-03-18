package com.samcomo.dbz.report.service;

import com.samcomo.dbz.report.model.dto.CustomSlice;
import com.samcomo.dbz.report.model.dto.ReportDto;
import com.samcomo.dbz.report.model.dto.ReportStateDto.Response;
import com.samcomo.dbz.report.model.dto.ReportSearchSummaryDto;
import com.samcomo.dbz.report.model.dto.ReportSummaryDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ReportService {

  ReportDto.Response uploadReport(long memberId, ReportDto.Form reportForm, List<MultipartFile> imageList);

  ReportDto.Response getReport(long reportId, long memberId);

  CustomSlice<ReportSummaryDto> getReportList(
      double lastLatitude, double lastLongitude, double curLatitude, double curLongitude, boolean showsInProcessOnly, Pageable pageable);

  ReportDto.Response updateReport(long reportId, long memberId, ReportDto.Form reportForm, List<MultipartFile> imageList);

  Response deleteReport(long reportId, long memberId);

  Response changeStatusToFound(long reportId, long memberId);

  CustomSlice<ReportSearchSummaryDto> searchReport(String object, boolean showsInProgressOnly, Pageable pageable);
}
