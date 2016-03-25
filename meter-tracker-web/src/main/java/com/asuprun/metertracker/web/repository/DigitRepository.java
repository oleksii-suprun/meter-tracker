package com.asuprun.metertracker.web.repository;

import com.asuprun.metertracker.web.domain.Digit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DigitRepository extends JpaRepository<Digit, Long> {
}
