package com.asuprun.metertracker.web.repository;

import com.asuprun.metertracker.web.domain.DashboardItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardItemRepository extends JpaRepository<DashboardItem, Long> {
}
