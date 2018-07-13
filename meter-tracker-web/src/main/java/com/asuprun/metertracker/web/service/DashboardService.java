package com.asuprun.metertracker.web.service;

import com.asuprun.metertracker.web.domain.DashboardItem;
import com.asuprun.metertracker.web.repository.DashboardItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DashboardService {

    private DashboardItemRepository dashboardItemRepository;

    @Autowired
    public DashboardService(DashboardItemRepository dashboardItemRepository) {
        this.dashboardItemRepository = dashboardItemRepository;
    }

    public DashboardItem findById(long id) {
        return dashboardItemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Dashboard item not found"));
    }

    public List<DashboardItem> findAll() {
        return dashboardItemRepository.findAll();
    }
}
