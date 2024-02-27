package com.samcomo.dbz.report.service;

import com.samcomo.dbz.report.model.dto.ReportForm;
import com.samcomo.dbz.report.model.dto.ReportResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ReportService {

  ReportResponse uploadReport(long memberId, ReportForm reportForm, List<MultipartFile> imageList);

}
