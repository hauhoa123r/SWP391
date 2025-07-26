package org.project.admin.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.project.admin.dto.request.ServiceRequest;
import org.project.admin.dto.response.ServiceResponse;
import org.project.admin.entity.Department;
import org.project.admin.entity.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-26T23:41:20+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class ServiceMapperImpl implements ServiceMapper {

    @Autowired
    private ServiceFeatureMapper serviceFeatureMapper;

    @Override
    public ServiceResponse toResponse(Service entity) {
        if ( entity == null ) {
            return null;
        }

        ServiceResponse serviceResponse = new ServiceResponse();

        serviceResponse.setDepartmentId( entityDepartmentDepartmentId( entity ) );
        serviceResponse.setFeatures( serviceFeatureMapper.toResponseList( entity.getFeatures() ) );
        serviceResponse.setServiceId( entity.getServiceId() );

        return serviceResponse;
    }

    @Override
    public List<ServiceResponse> toResponseList(List<Service> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ServiceResponse> list = new ArrayList<ServiceResponse>( entities.size() );
        for ( Service service : entities ) {
            list.add( toResponse( service ) );
        }

        return list;
    }

    @Override
    public Service toEntity(ServiceRequest request) {
        if ( request == null ) {
            return null;
        }

        Service service = new Service();

        return service;
    }

    private Long entityDepartmentDepartmentId(Service service) {
        if ( service == null ) {
            return null;
        }
        Department department = service.getDepartment();
        if ( department == null ) {
            return null;
        }
        Long departmentId = department.getDepartmentId();
        if ( departmentId == null ) {
            return null;
        }
        return departmentId;
    }
}
