package org.project.admin.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.project.admin.dto.request.HospitalRequest;
import org.project.admin.dto.response.HospitalResponse;
import org.project.admin.entity.Hospital;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-24T11:18:08+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class HospitalMapperImpl implements HospitalMapper {

    @Override
    public HospitalResponse toResponse(Hospital entity) {
        if ( entity == null ) {
            return null;
        }

        HospitalResponse hospitalResponse = new HospitalResponse();

        hospitalResponse.setHospitalId( entity.getHospitalId() );
        hospitalResponse.setName( entity.getName() );
        hospitalResponse.setAddress( entity.getAddress() );
        hospitalResponse.setPhoneNumber( entity.getPhoneNumber() );
        hospitalResponse.setEmail( entity.getEmail() );
        hospitalResponse.setAvatarUrl( entity.getAvatarUrl() );

        return hospitalResponse;
    }

    @Override
    public List<HospitalResponse> toResponseList(List<Hospital> entities) {
        if ( entities == null ) {
            return null;
        }

        List<HospitalResponse> list = new ArrayList<HospitalResponse>( entities.size() );
        for ( Hospital hospital : entities ) {
            list.add( toResponse( hospital ) );
        }

        return list;
    }

    @Override
    public Hospital toEntity(HospitalRequest request) {
        if ( request == null ) {
            return null;
        }

        Hospital hospital = new Hospital();

        hospital.setName( request.getName() );
        hospital.setAddress( request.getAddress() );
        hospital.setPhoneNumber( request.getPhoneNumber() );
        hospital.setEmail( request.getEmail() );
        hospital.setAvatarUrl( request.getAvatarUrl() );

        return hospital;
    }
}
