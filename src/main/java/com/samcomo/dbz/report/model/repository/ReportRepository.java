package com.samcomo.dbz.report.model.repository;

import com.samcomo.dbz.report.model.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

}
