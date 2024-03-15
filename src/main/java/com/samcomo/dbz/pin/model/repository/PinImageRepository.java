package com.samcomo.dbz.pin.model.repository;

import com.samcomo.dbz.pin.model.entity.PinImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PinImageRepository extends JpaRepository<PinImage,Long> {

}
