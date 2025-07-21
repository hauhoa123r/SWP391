package org.project.service.impl;

import org.project.converter.DoctorsConverter;
import org.project.entity.DoctorEntity;
import org.project.entity.StaffEntity;
import org.project.model.response.DoctorHeaderResponse;
import org.project.repository.DoctorVRepository;
import org.project.repository.StaffVRepository;
import org.project.service.DoctorVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorVServiceImpl implements DoctorVService {
    @Autowired
    private DoctorVRepository doctorRepository;
    @Autowired
    private StaffVRepository staffRepository;
    @Autowired
    private DoctorsConverter doctorConverter;
    @Override
    public DoctorHeaderResponse getDoctorByUserId(Long userId) {
        StaffEntity staff = staffRepository.findByUserEntityId(userId);
        if (staff == null) return null;
        DoctorEntity doctor = doctorRepository.findByStaffEntityId(staff.getId());
        return doctor != null ? doctorConverter.toDoctorResponse(doctor) : null;
    }
}
