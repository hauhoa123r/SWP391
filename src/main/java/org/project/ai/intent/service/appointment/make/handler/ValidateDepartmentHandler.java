package org.project.ai.intent.service.appointment.make.handler;

import org.project.ai.chat.AIService;
import org.project.entity.DepartmentEntity;
import org.project.model.dai.AppointmentDAI;
import org.project.repository.DepartmentRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidateDepartmentHandler extends BaseValidationHandler {

    private final DepartmentRepository departmentRepository;

    public ValidateDepartmentHandler(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    protected boolean isValid(AppointmentDAI data) {
        if (data.getDepartmentName() == null || data.getHospitalName().isBlank()) return false;
        DepartmentEntity departmentEntity = departmentRepository.findByNameContaining(data.getDepartmentName());
        return departmentEntity != null;
    }

    @Override
    protected String errorMessage(AppointmentDAI data) {
        if (data.getDepartmentName() == null || data.getHospitalName().isBlank()) {
            return "Bạn chưa chọn chuyên khoa. Bạn muốn khám khoa nào?";
        }
        return "Người dùng chưa chọn khoa bạn yêu cầu người dùng chọn khoa cho tôi";
    }
}
