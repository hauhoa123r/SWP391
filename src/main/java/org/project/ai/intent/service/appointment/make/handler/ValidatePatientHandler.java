package org.project.ai.intent.service.appointment.make.handler;

import org.project.ai.chat.AIService;
import org.project.entity.PatientEntity;
import org.project.model.dai.AppointmentDAI;
import org.project.repository.PatientRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidatePatientHandler extends BaseValidationHandler{

    private final PatientRepository patientRepository;

    public ValidatePatientHandler(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    protected boolean isValid(AppointmentDAI data) {
        if(data.getPatientName() == null || data.getPatientName().isBlank()) return false;
        PatientEntity patientEntity = patientRepository.findByUserEntity_IdAndFullName(data.getUserId(), data.getPatientName());
        return patientEntity != null;
    }

    @Override
    protected String errorMessage(AppointmentDAI data) {
        if(data.getPatientName() == null || data.getPatientName().isBlank()){
            return "Bạn muốn đặt lịch khám cho bệnh nhân nào?, nếu chưa từng khám ở các bệnh viện trong hệ thống thì hãy cho tôi những thông tin sau:" +
                    "Họ và tên:" +
                    "Email:" +
                    "Số điện thoại:" +
                    "Địa chỉ:" +
                    "Giới tính:" +
                    "Ngày sinh:" +
                    "Mối quan hệ trong gia đình(bố, mẹ, anh, chị...):" +
                    "Loại máu:";
        }
        return "Đặt lịch thành công";
    }
}
