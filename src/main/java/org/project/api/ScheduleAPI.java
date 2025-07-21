package org.project.api;

import org.project.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleAPI {

    private ScheduleService scheduleService;

    @Autowired
    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/staff/{staffId}/patient/{patientId}/date/{availableDate}")
    public ResponseEntity<Map<String, Object>> getAvailableTimes(@PathVariable Long staffId, @PathVariable Long patientId,
                                                                 @PathVariable Date availableDate) {
        return ResponseEntity.ok(
                Map.of(
                        "availableTimes", scheduleService.getAvailableTimes(staffId, patientId, availableDate)
                )
        );
    }
}
