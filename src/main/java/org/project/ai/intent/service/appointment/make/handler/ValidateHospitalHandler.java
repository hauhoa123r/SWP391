package org.project.ai.intent.service.appointment.make.handler;

import org.project.entity.HospitalEntity;
import org.project.model.dai.AppointmentDAI;
import org.project.repository.HospitalRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidateHospitalHandler extends BaseValidationHandler{

    private final HospitalRepository hospitalRepository;

    public ValidateHospitalHandler(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    @Override
    protected boolean isValid(AppointmentDAI data) {
        if (data.getHospitalName() == null || data.getHospitalName().isBlank()) return false;
        HospitalEntity hospital = hospitalRepository.findByNameContaining(data.getHospitalName());
        if (hospital != null) data.setHospitalId(hospital.getId());
        return hospital != null;
    }

    @Override
    protected String errorMessage(AppointmentDAI data) {
        if (data.getHospitalName() == null || data.getHospitalName().isBlank()) {
            return "Bạn chưa chọn bệnh viện. Bạn muốn đặt tại bệnh viện nào?";
        }
        return "Bệnh viện " + data.getHospitalName() + " không tồn tại trong hệ thống, vui lòng nhập lại.";
    }
}
