package com.asuprun.metertracker.web.resource.impl;

import com.asuprun.metertracker.web.domain.Meter;
import com.asuprun.metertracker.web.resource.MeterResource;
import com.asuprun.metertracker.web.sevice.MeterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MeterResourceImpl implements MeterResource {

    @Autowired
    private MeterService meterService;

    @Override
    public List<Meter> getAll() {
        return meterService.findAll();
    }
}
