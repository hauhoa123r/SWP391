package org.project.mapper;

import javax.annotation.processing.Generated;
import org.project.entity.PatientEntity;
import org.project.entity.UserEntity;
import org.project.enums.UserStatus;
import org.project.model.request.AdminPatientUpdateRequest;
import org.project.model.response.AdminPatientDetailResponse;
import org.project.model.response.AdminPatientResponse;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-25T22:32:21+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class AdminPatientMapperImpl implements AdminPatientMapper {

    @Override
    public AdminPatientResponse toResponse(PatientEntity patient) {
        if ( patient == null ) {
            return null;
        }

        AdminPatientResponse adminPatientResponse = new AdminPatientResponse();

        adminPatientResponse.setId( patient.getId() );
        adminPatientResponse.setFullName( patient.getFullName() );
        adminPatientResponse.setEmail( patient.getEmail() );
        adminPatientResponse.setPhoneNumber( patient.getPhoneNumber() );
        adminPatientResponse.setAddress( patient.getAddress() );
        adminPatientResponse.setGender( patient.getGender() );
        if ( patient.getFamilyRelationship() != null ) {
            adminPatientResponse.setFamilyRelationship( patient.getFamilyRelationship().name() );
        }
        adminPatientResponse.setBloodType( patient.getBloodType() );
        UserStatus userStatus = patientUserEntityUserStatus( patient );
        if ( userStatus != null ) {
            adminPatientResponse.setStatus( userStatus.name() );
        }

        adminPatientResponse.setDateOfBirth( patient.getBirthdate().toLocalDate() );
        adminPatientResponse.setAge( java.time.Period.between(patient.getBirthdate().toLocalDate(), java.time.LocalDate.now()).getYears() );

        return adminPatientResponse;
    }

    @Override
    public AdminPatientDetailResponse toDetailResponse(PatientEntity patient) {
        if ( patient == null ) {
            return null;
        }

        AdminPatientDetailResponse.AdminPatientDetailResponseBuilder adminPatientDetailResponse = AdminPatientDetailResponse.builder();

        adminPatientDetailResponse.id( patient.getId() );
        adminPatientDetailResponse.fullName( patient.getFullName() );
        adminPatientDetailResponse.email( patient.getEmail() );
        adminPatientDetailResponse.phoneNumber( patient.getPhoneNumber() );
        adminPatientDetailResponse.address( patient.getAddress() );
        adminPatientDetailResponse.gender( patient.getGender() );
        adminPatientDetailResponse.familyRelationship( patient.getFamilyRelationship() );
        adminPatientDetailResponse.bloodType( patient.getBloodType() );
        adminPatientDetailResponse.avatarUrl( patient.getAvatarUrl() );

        return adminPatientDetailResponse.build();
    }

    @Override
    public AdminPatientUpdateRequest toUpdateRequest(PatientEntity patient) {
        if ( patient == null ) {
            return null;
        }

        AdminPatientUpdateRequest adminPatientUpdateRequest = new AdminPatientUpdateRequest();

        adminPatientUpdateRequest.setFullName( patient.getFullName() );
        adminPatientUpdateRequest.setPhoneNumber( patient.getPhoneNumber() );
        adminPatientUpdateRequest.setEmail( patient.getEmail() );
        adminPatientUpdateRequest.setAddress( patient.getAddress() );
        adminPatientUpdateRequest.setGender( patient.getGender() );
        adminPatientUpdateRequest.setFamilyRelationship( patient.getFamilyRelationship() );
        adminPatientUpdateRequest.setBloodType( patient.getBloodType() );

        return adminPatientUpdateRequest;
    }

    @Override
    public void updatePatientFromRequest(AdminPatientUpdateRequest request, PatientEntity entity) {
        if ( request == null ) {
            return;
        }

        entity.setPhoneNumber( request.getPhoneNumber() );
        entity.setEmail( request.getEmail() );
        entity.setFullName( request.getFullName() );
        entity.setAddress( request.getAddress() );
        entity.setFamilyRelationship( request.getFamilyRelationship() );
        entity.setGender( request.getGender() );
        entity.setBloodType( request.getBloodType() );
    }

    private UserStatus patientUserEntityUserStatus(PatientEntity patientEntity) {
        if ( patientEntity == null ) {
            return null;
        }
        UserEntity userEntity = patientEntity.getUserEntity();
        if ( userEntity == null ) {
            return null;
        }
        UserStatus userStatus = userEntity.getUserStatus();
        if ( userStatus == null ) {
            return null;
        }
        return userStatus;
    }
}
