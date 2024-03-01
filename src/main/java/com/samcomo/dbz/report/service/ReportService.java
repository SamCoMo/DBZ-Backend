package com.samcomo.dbz.report.service;

import com.samcomo.dbz.report.model.dto.ReportDto;
import com.samcomo.dbz.report.model.dto.ReportList;
import com.samcomo.dbz.report.model.dto.ReportStateDto.Response;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

public interface ReportService {

  ReportDto.Response uploadReport(long memberId, ReportDto.Form reportForm, List<MultipartFile> imageList);

  ReportDto.Response getReport(long reportId);

  Slice<ReportList> getReportList(
      long cursorId, double latitude, double longitude, boolean showsInProcessOnly, Pageable pageable);

  ReportDto.Response updateReport(long reportId, ReportDto.Form reportForm, List<MultipartFile> imageList, long userId);

  Response deleteReport(long userId, long reportId);

  Response changeStatusToFound(long userId, long reportId);

  Slice<ReportList> search(String object, boolean showsInProgressOnly, Pageable pageable);
}
