package com.asuprun.metertracker.web.repository;

import com.asuprun.metertracker.web.domain.AssetBinding;
import com.asuprun.metertracker.web.domain.AssetBindingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceBindingRepository extends JpaRepository<AssetBinding, AssetBindingId> {
}
