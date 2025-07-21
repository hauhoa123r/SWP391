package org.project.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public interface ScheduleService {
    List<Timestamp> getAvailableTimes(Long staffId, Long patientId, Date availableDate);
}
