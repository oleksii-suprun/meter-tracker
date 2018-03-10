package com.asuprun.metertracker.web.repository;

import com.asuprun.metertracker.web.domain.ImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageInfoRepository extends JpaRepository<ImageInfo, Long> {
}
