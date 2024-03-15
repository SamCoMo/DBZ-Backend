package com.samcomo.dbz.report.model.repository;

import com.samcomo.dbz.report.model.entity.Report;
import com.samcomo.dbz.report.model.entity.ReportImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportImageRepository extends JpaRepository<ReportImage, Long> {

  List<ReportImage> findAllByReport(Report report);

  Optional<ReportImage> findFirstByReport(Report report);
}
