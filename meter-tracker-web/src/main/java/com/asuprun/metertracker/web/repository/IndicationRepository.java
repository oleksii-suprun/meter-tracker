package com.asuprun.metertracker.web.repository;

import com.asuprun.metertracker.web.domain.Indication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndicationRepository extends JpaRepository<Indication, Long> {

    List<Indication> findByValueIsNull();

    List<Indication> findByValueIsNotNull();

    List<Indication> findByMeterIdAndValueIsNotNull(long meterId);

    List<Indication> findByMeterIdAndValueIsNull(long meterId);

    Optional<Indication> findByHash(String hash);
}
