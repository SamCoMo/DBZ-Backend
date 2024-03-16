package com.samcomo.dbz.pin.model.repository;

import com.samcomo.dbz.pin.model.entity.PinImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PinImageRepository extends JpaRepository<PinImage,Long> {
  List<PinImage> findAllByPinId(Long pinId);
}
