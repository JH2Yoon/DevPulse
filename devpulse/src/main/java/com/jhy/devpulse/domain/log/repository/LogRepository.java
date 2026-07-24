package com.jhy.devpulse.domain.log.repository;

import com.jhy.devpulse.domain.log.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {

}
