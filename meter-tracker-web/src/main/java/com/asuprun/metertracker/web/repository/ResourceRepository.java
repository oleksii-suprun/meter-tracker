package com.asuprun.metertracker.web.repository;

import com.asuprun.metertracker.web.domain.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findById(long id);
}
