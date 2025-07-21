package org.project.api;

import org.project.service.AppointmentService;
import org.project.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleAPI {

    private ScheduleService scheduleService;

    private AppointmentService appointmentService;

    @Autowired
    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Autowired
    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
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

    @PostMapping("/staff/{staffId}/patient/{patientId}/date/{availableDate}")
    public ResponseEntity<Map<String, Object>> getAvailableTimesForSuggestion(@PathVariable Long staffId, @PathVariable Long patientId,
                                                                              @PathVariable String availableDate) {
        try {

            Timestamp timestamp = Timestamp.valueOf(availableDate.replace("T", " "));
            return ResponseEntity.ok(
                    Map.of(
                            "availableTimes", appointmentService.getAvailableAppointmentsByDoctorIdForSuggestion(staffId, patientId, timestamp, 30)
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
