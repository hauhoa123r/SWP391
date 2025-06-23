package org.project.service.impl;

import org.project.entity.AppointmentEntity;
import org.project.entity.StaffScheduleEntity;
import org.project.repository.AppointmentRepository;
import org.project.repository.StaffScheduleRepository;
import org.project.service.ScheduleService;
import org.project.utils.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    private StaffScheduleRepository staffScheduleRepository;
    private AppointmentRepository appointmentRepository;
    private TimestampUtils timestampUtils;

    @Autowired
    public void setStaffScheduleRepository(StaffScheduleRepository staffScheduleRepository) {
        this.staffScheduleRepository = staffScheduleRepository;
    }

    @Autowired
    public void setAppointmentRepository(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Autowired
    public void setTimestampUtils(TimestampUtils timestampUtils) {
        this.timestampUtils = timestampUtils;
    }

    @Override
    public List<Timestamp> getAvailableTimes(Long staffId, Date availableDate) {
        timestampUtils = new TimestampUtils(availableDate);
        if (availableDate.getTime() == timestampUtils.getStartOfDay().getTime()) {
            timestampUtils = new TimestampUtils();
        }

        List<StaffScheduleEntity> staffScheduleEntities = staffScheduleRepository.findByStaffEntityIdAndAvailableDate(staffId, availableDate);
        List<AppointmentEntity> appointmentEntities = appointmentRepository.findByDoctorEntityStaffEntityIdAndStartTimeBetween(staffId,
                timestampUtils.getTimestamp(), timestampUtils.getEndOfDay());

        List<Timestamp> availableTimes = new ArrayList<>();
        for (StaffScheduleEntity schedule : staffScheduleEntities) {
            Timestamp startTime = schedule.getStartTime();
            Timestamp endTime = schedule.getEndTime();

            while (startTime.before(endTime)) {
                if (timestampUtils.isBefore(startTime)) {
                    availableTimes.add(startTime);
                }
                startTime = timestampUtils.plusMinutes(startTime, 30);
            }
        }

        for (int i = 0; i < availableTimes.size(); i++) {
            Timestamp time = availableTimes.get(i);
            for (AppointmentEntity appointment : appointmentEntities) {
                if (appointment.getStartTime().equals(time)) {
                    availableTimes.remove(i);
                    i--; // Adjust index after removal
                    break;
                }
            }
        }

        return availableTimes;
    }
}