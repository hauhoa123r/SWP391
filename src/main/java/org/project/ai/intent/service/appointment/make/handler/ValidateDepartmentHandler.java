package org.project.ai.intent.service.appointment.make.handler;

import org.project.ai.chat.AIService;
import org.project.entity.DepartmentEntity;
import org.project.model.dai.AppointmentDAI;
import org.project.repository.DepartmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
        if(departmentEntity != null) data.setDepartmentId(departmentEntity.getId());
        return departmentEntity != null;
    }

    @Override
    protected String errorMessage(AppointmentDAI data) {
        List<DepartmentEntity> departments = departmentRepository.findAll();
        if (data.getDepartmentName() == null || data.getHospitalName().isBlank()) {
            return "Bạn chưa chọn chuyên khoa. Bạn muốn khám khoa nào?";
        }
        return """
                Khoa %s không tồn tại trong hệ thống chúng tôi
                Đây là danh sách khoa: 
                %s
                """.formatted(data.getDepartmentName(), listDepartments(departments));
    }

    private String listDepartments(List<DepartmentEntity> departments) {
        return departments.stream().map(DepartmentEntity::getName).collect(Collectors.joining("\n"));
    }
}
