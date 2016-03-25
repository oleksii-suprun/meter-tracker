package com.asuprun.metertracker.web.repository;

import com.asuprun.metertracker.web.domain.ResourceBinding;
import com.asuprun.metertracker.web.domain.ResourceBindingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceBindingRepository extends JpaRepository<ResourceBinding, ResourceBindingId> {
}
