package com.asuprun.metertracker.web.resource.impl;

import com.asuprun.metertracker.web.domain.DashboardItem;
import com.asuprun.metertracker.web.domain.Indication;
import com.asuprun.metertracker.web.dto.ConsumptionDataDto;
import com.asuprun.metertracker.web.resource.DashboardResource;
import com.asuprun.metertracker.web.service.DashboardService;
import com.asuprun.metertracker.web.service.IndicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DashboardResourceImpl implements DashboardResource {

    private DashboardService dashboardService;
    private IndicationService indicationService;

    @Autowired
    public DashboardResourceImpl(DashboardService dashboardService,
                                 IndicationService indicationService) {
        this.dashboardService = dashboardService;
        this.indicationService = indicationService;
    }

    @Override
    public List<DashboardItem> getItems() {
        return dashboardService.findAll();
    }

    @Override
    public List<ConsumptionDataDto> consumptionDataSeries(long dashboardItemId) {
        return dashboardService.findById(dashboardItemId).getEntries().stream()
                .map(entry -> entry.getMeter().getId())
                .map(this::consumptionDotByMeterId)
                .collect(Collectors.toList());
    }

    private ConsumptionDataDto consumptionDotByMeterId(long meterId) {
        List<ConsumptionDataDto.Entry> entries = indicationService.findByTypeAndState(meterId, false).stream()
                .map(this::dtoEntryByIndication)
                .collect(Collectors.toList());

        ConsumptionDataDto consumptionDataDto = new ConsumptionDataDto();
        consumptionDataDto.setMeterId(meterId);
        consumptionDataDto.setSeries(entries);
        return consumptionDataDto;
    }

    private ConsumptionDataDto.Entry dtoEntryByIndication(Indication indication) {
        ConsumptionDataDto.Entry entry = new ConsumptionDataDto.Entry();
        entry.setDate(indication.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        entry.setValue(indication.getConsumption());
        return entry;
    }
}
