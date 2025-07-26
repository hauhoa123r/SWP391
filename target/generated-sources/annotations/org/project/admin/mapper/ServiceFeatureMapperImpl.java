package org.project.admin.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.project.admin.dto.request.ServiceFeatureRequest;
import org.project.admin.dto.response.ServiceFeatureResponse;
import org.project.admin.entity.ServiceFeature;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-25T22:32:21+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class ServiceFeatureMapperImpl implements ServiceFeatureMapper {

    @Override
    public ServiceFeatureResponse toResponse(ServiceFeature entity) {
        if ( entity == null ) {
            return null;
        }

        ServiceFeatureResponse serviceFeatureResponse = new ServiceFeatureResponse();

        serviceFeatureResponse.setServiceFeatureId( entity.getServiceFeatureId() );
        serviceFeatureResponse.setName( entity.getName() );
        serviceFeatureResponse.setDescription( entity.getDescription() );

        return serviceFeatureResponse;
    }

    @Override
    public List<ServiceFeatureResponse> toResponseList(List<ServiceFeature> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ServiceFeatureResponse> list = new ArrayList<ServiceFeatureResponse>( entities.size() );
        for ( ServiceFeature serviceFeature : entities ) {
            list.add( toResponse( serviceFeature ) );
        }

        return list;
    }

    @Override
    public ServiceFeature toEntity(ServiceFeatureRequest request) {
        if ( request == null ) {
            return null;
        }

        ServiceFeature serviceFeature = new ServiceFeature();

        serviceFeature.setName( request.getName() );
        serviceFeature.setDescription( request.getDescription() );

        return serviceFeature;
    }
}
