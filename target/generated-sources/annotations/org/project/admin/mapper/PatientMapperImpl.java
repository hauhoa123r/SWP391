package org.project.admin.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.project.admin.dto.request.PatientRequest;
import org.project.admin.dto.response.PatientResponse;
import org.project.admin.entity.Patient;
import org.project.admin.entity.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-25T22:32:21+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class PatientMapperImpl implements PatientMapper {

    @Override
    public PatientResponse toResponse(Patient entity) {
        if ( entity == null ) {
            return null;
        }

        PatientResponse patientResponse = new PatientResponse();

        patientResponse.setUserId( entityUserUserId( entity ) );
        patientResponse.setPatientId( entity.getPatientId() );
        patientResponse.setPhoneNumber( entity.getPhoneNumber() );
        patientResponse.setEmail( entity.getEmail() );
        patientResponse.setFullName( entity.getFullName() );
        patientResponse.setAvatarUrl( entity.getAvatarUrl() );
        patientResponse.setRelationship( entity.getRelationship() );
        patientResponse.setAddress( entity.getAddress() );
        patientResponse.setGender( entity.getGender() );
        patientResponse.setBirthdate( entity.getBirthdate() );
        patientResponse.setBloodType( entity.getBloodType() );

        return patientResponse;
    }

    @Override
    public List<PatientResponse> toResponseList(List<Patient> entities) {
        if ( entities == null ) {
            return null;
        }

        List<PatientResponse> list = new ArrayList<PatientResponse>( entities.size() );
        for ( Patient patient : entities ) {
            list.add( toResponse( patient ) );
        }

        return list;
    }

    @Override
    public Patient toEntity(PatientRequest request) {
        if ( request == null ) {
            return null;
        }

        Patient patient = new Patient();

        patient.setPhoneNumber( request.getPhoneNumber() );
        patient.setEmail( request.getEmail() );
        patient.setFullName( request.getFullName() );
        patient.setAvatarUrl( request.getAvatarUrl() );
        patient.setRelationship( request.getRelationship() );
        patient.setAddress( request.getAddress() );
        patient.setGender( request.getGender() );
        patient.setBirthdate( request.getBirthdate() );
        patient.setBloodType( request.getBloodType() );

        return patient;
    }

    private Long entityUserUserId(Patient patient) {
        if ( patient == null ) {
            return null;
        }
        User user = patient.getUser();
        if ( user == null ) {
            return null;
        }
        Long userId = user.getUserId();
        if ( userId == null ) {
            return null;
        }
        return userId;
    }
}
