package com.samcomo.dbz.pin.model.repository;

import com.samcomo.dbz.pin.model.entity.Pin;
import com.samcomo.dbz.report.model.entity.Report;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PinRepository extends JpaRepository<Pin, Long> {

  List<Pin> findByReport(Report report);
}
