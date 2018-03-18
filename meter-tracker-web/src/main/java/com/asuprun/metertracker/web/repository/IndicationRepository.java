package com.asuprun.metertracker.web.repository;

import com.asuprun.metertracker.web.domain.Indication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface IndicationRepository extends JpaRepository<Indication, Long>, JpaSpecificationExecutor<Indication> {

    Optional<Indication> findById(long id);

    @Query(value = "SELECT * FROM indication i WHERE i.meter_id = :meterId AND i.value IS NOT NULL AND i.created_at < :date ORDER BY i.created_at DESC LIMIT 1", nativeQuery = true)
    Optional<Indication> findRecognizedBefore(@Param("meterId") long meterId, @Param("date") Date date);

    @Query(value = "SELECT * FROM indication i WHERE i.meter_id = :meterId AND i.value IS NOT NULL AND i.created_at > :date ORDER BY i.created_at LIMIT 1", nativeQuery = true)
    Optional<Indication> findRecognizedAfter(@Param("meterId") long meterId, @Param("date") Date date);
}
