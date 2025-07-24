package org.project.admin.mapper;

import javax.annotation.processing.Generated;
import org.project.admin.dto.request.DoctorRequest;
import org.project.admin.dto.response.DoctorResponse;
import org.project.admin.entity.Doctor;
import org.project.admin.entity.Staff;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-24T11:18:08+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class DoctorMapperImpl implements DoctorMapper {

    @Override
    public Doctor toEntity(DoctorRequest request) {
        if ( request == null ) {
            return null;
        }

        Doctor doctor = new Doctor();

        doctor.setDoctorId( request.getStaffId() );

        doctor.setDoctorRank( mapRankLevelToEnum(request.getRankLevel()) );

        return doctor;
    }

    @Override
    public DoctorResponse toResponse(Doctor doctor, Staff staff) {
        if ( doctor == null && staff == null ) {
            return null;
        }

        DoctorResponse doctorResponse = new DoctorResponse();

        if ( doctor != null ) {
            doctorResponse.setDoctorId( doctor.getDoctorId() );
            doctorResponse.setDoctorRank( doctor.getDoctorRank() );
        }
        if ( staff != null ) {
            doctorResponse.setFullName( staff.getFullName() );
            doctorResponse.setAvatarUrl( staff.getAvatarUrl() );
        }

        return doctorResponse;
    }
}
