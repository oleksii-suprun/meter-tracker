package com.asuprun.metertracker.web.service;

import com.asuprun.metertracker.web.domain.Meter;
import com.asuprun.metertracker.web.repository.MeterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MeterService {

    @Autowired
    private MeterRepository meterRepository;

    public Optional<Meter> findOne(long id) {
        return meterRepository.findById(id);
    }

    public List<Meter> findAll() {
        return meterRepository.findAll();
    }
}
