package com.samcomo.dbz.report.service;

import com.samcomo.dbz.report.model.dto.CustomSlice;
import com.samcomo.dbz.report.model.dto.ReportDto;
import com.samcomo.dbz.report.model.dto.ReportList;
import com.samcomo.dbz.report.model.dto.ReportStateDto.Response;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ReportService {

  ReportDto.Response uploadReport(long memberId, ReportDto.Form reportForm, List<MultipartFile> imageList);

  ReportDto.Response getReport(long reportId);
  CustomSlice<ReportList> getReportList(
      double lastLatitude, double lastLongitude, double curLatitude, double curLongitude, boolean showsInProcessOnly, Pageable pageable);

  ReportDto.Response updateReport(long reportId, ReportDto.Form reportForm, List<MultipartFile> imageList, long userId);

  Response deleteReport(long userId, long reportId);

  Response changeStatusToFound(long userId, long reportId);

  CustomSlice<ReportList> searchReport(String object, boolean showsInProgressOnly, Pageable pageable);
}
