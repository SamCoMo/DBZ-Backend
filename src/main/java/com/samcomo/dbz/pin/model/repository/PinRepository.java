package com.samcomo.dbz.pin.model.repository;

import com.samcomo.dbz.pin.model.entity.Pin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PinRepository extends JpaRepository<Pin, Long> {

}
