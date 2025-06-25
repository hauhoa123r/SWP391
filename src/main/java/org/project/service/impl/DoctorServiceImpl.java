package org.project.service.impl;

import org.project.converter.DoctorConverter;
import org.project.entity.DoctorEntity;
import org.project.entity.StaffEntity;
import org.project.model.response.DoctorResponse;
import org.project.repository.DoctorRepository;
import org.project.repository.StaffRepository;
import org.project.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceImpl implements DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private DoctorConverter doctorConverter;
    @Override
    public DoctorResponse getDoctorByUserId(Long userId) {
        StaffEntity staff = staffRepository.findByUserEntityId(userId);
        if (staff == null) return null;
        DoctorEntity doctor = doctorRepository.findByStaffEntityId(staff.getId());
        return doctor != null ? doctorConverter.toDoctorResponse(doctor) : null;
    }
}
