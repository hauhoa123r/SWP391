package org.project.admin.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.project.admin.dto.request.DepartmentRequest;
import org.project.admin.dto.response.DepartmentResponse;
import org.project.admin.entity.Department;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-26T23:41:20+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class DepartmentMapperImpl implements DepartmentMapper {

    @Override
    public DepartmentResponse toResponse(Department entity) {
        if ( entity == null ) {
            return null;
        }

        DepartmentResponse departmentResponse = new DepartmentResponse();

        departmentResponse.setDepartmentId( entity.getDepartmentId() );
        departmentResponse.setName( entity.getName() );
        departmentResponse.setDescription( entity.getDescription() );
        departmentResponse.setVideoUrl( entity.getVideoUrl() );
        departmentResponse.setBannerUrl( entity.getBannerUrl() );
        departmentResponse.setSlogan( entity.getSlogan() );

        return departmentResponse;
    }

    @Override
    public List<DepartmentResponse> toResponseList(List<Department> entities) {
        if ( entities == null ) {
            return null;
        }

        List<DepartmentResponse> list = new ArrayList<DepartmentResponse>( entities.size() );
        for ( Department department : entities ) {
            list.add( toResponse( department ) );
        }

        return list;
    }

    @Override
    public Department toEntity(DepartmentRequest request) {
        if ( request == null ) {
            return null;
        }

        Department department = new Department();

        department.setName( request.getName() );
        department.setDescription( request.getDescription() );
        department.setVideoUrl( request.getVideoUrl() );
        department.setBannerUrl( request.getBannerUrl() );
        department.setSlogan( request.getSlogan() );

        return department;
    }
}
