package org.project.admin.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.project.admin.dto.request.AppointmentRequest;
import org.project.admin.dto.response.AppointmentResponse;
import org.project.admin.dto.response.ServiceFeatureResponse;
import org.project.admin.entity.Appointment;
import org.project.admin.entity.Patient;
import org.project.admin.entity.Service;
import org.project.admin.entity.ServiceFeature;
import org.project.admin.entity.Staff;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-25T22:32:22+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class AppointmentMapperImpl implements AppointmentMapper {

    @Override
    public AppointmentResponse toResponse(Appointment entity) {
        if ( entity == null ) {
            return null;
        }

        AppointmentResponse appointmentResponse = new AppointmentResponse();

        appointmentResponse.setDoctorId( entityDoctorStaffId( entity ) );
        appointmentResponse.setDoctorName( entityDoctorFullName( entity ) );
        appointmentResponse.setPatientId( entityPatientPatientId( entity ) );
        appointmentResponse.setPatientName( entityPatientFullName( entity ) );
        appointmentResponse.setServiceId( entityServiceServiceId( entity ) );
        List<ServiceFeature> features = entityServiceFeatures( entity );
        appointmentResponse.setServiceFeatures( serviceFeatureListToServiceFeatureResponseList( features ) );
        appointmentResponse.setSchedulingCoordinatorId( entitySchedulingCoordinatorStaffId( entity ) );
        appointmentResponse.setSchedulingCoordinatorName( entitySchedulingCoordinatorFullName( entity ) );
        appointmentResponse.setAppointmentId( entity.getAppointmentId() );
        appointmentResponse.setStartTime( entity.getStartTime() );
        appointmentResponse.setDurationMinutes( entity.getDurationMinutes() );
        appointmentResponse.setAppointmentStatus( entity.getAppointmentStatus() );

        return appointmentResponse;
    }

    @Override
    public List<AppointmentResponse> toResponseList(List<Appointment> entities) {
        if ( entities == null ) {
            return null;
        }

        List<AppointmentResponse> list = new ArrayList<AppointmentResponse>( entities.size() );
        for ( Appointment appointment : entities ) {
            list.add( toResponse( appointment ) );
        }

        return list;
    }

    @Override
    public Appointment toEntity(AppointmentRequest req) {
        if ( req == null ) {
            return null;
        }

        Appointment appointment = new Appointment();

        appointment.setStartTime( req.getStartTime() );
        appointment.setDurationMinutes( req.getDurationMinutes() );
        appointment.setAppointmentStatus( req.getAppointmentStatus() );

        return appointment;
    }

    private Long entityDoctorStaffId(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }
        Staff doctor = appointment.getDoctor();
        if ( doctor == null ) {
            return null;
        }
        Long staffId = doctor.getStaffId();
        if ( staffId == null ) {
            return null;
        }
        return staffId;
    }

    private String entityDoctorFullName(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }
        Staff doctor = appointment.getDoctor();
        if ( doctor == null ) {
            return null;
        }
        String fullName = doctor.getFullName();
        if ( fullName == null ) {
            return null;
        }
        return fullName;
    }

    private Long entityPatientPatientId(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }
        Patient patient = appointment.getPatient();
        if ( patient == null ) {
            return null;
        }
        Long patientId = patient.getPatientId();
        if ( patientId == null ) {
            return null;
        }
        return patientId;
    }

    private String entityPatientFullName(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }
        Patient patient = appointment.getPatient();
        if ( patient == null ) {
            return null;
        }
        String fullName = patient.getFullName();
        if ( fullName == null ) {
            return null;
        }
        return fullName;
    }

    private Long entityServiceServiceId(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }
        Service service = appointment.getService();
        if ( service == null ) {
            return null;
        }
        Long serviceId = service.getServiceId();
        if ( serviceId == null ) {
            return null;
        }
        return serviceId;
    }

    private List<ServiceFeature> entityServiceFeatures(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }
        Service service = appointment.getService();
        if ( service == null ) {
            return null;
        }
        List<ServiceFeature> features = service.getFeatures();
        if ( features == null ) {
            return null;
        }
        return features;
    }

    protected ServiceFeatureResponse serviceFeatureToServiceFeatureResponse(ServiceFeature serviceFeature) {
        if ( serviceFeature == null ) {
            return null;
        }

        ServiceFeatureResponse serviceFeatureResponse = new ServiceFeatureResponse();

        serviceFeatureResponse.setServiceFeatureId( serviceFeature.getServiceFeatureId() );
        serviceFeatureResponse.setName( serviceFeature.getName() );
        serviceFeatureResponse.setDescription( serviceFeature.getDescription() );

        return serviceFeatureResponse;
    }

    protected List<ServiceFeatureResponse> serviceFeatureListToServiceFeatureResponseList(List<ServiceFeature> list) {
        if ( list == null ) {
            return null;
        }

        List<ServiceFeatureResponse> list1 = new ArrayList<ServiceFeatureResponse>( list.size() );
        for ( ServiceFeature serviceFeature : list ) {
            list1.add( serviceFeatureToServiceFeatureResponse( serviceFeature ) );
        }

        return list1;
    }

    private Long entitySchedulingCoordinatorStaffId(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }
        Staff schedulingCoordinator = appointment.getSchedulingCoordinator();
        if ( schedulingCoordinator == null ) {
            return null;
        }
        Long staffId = schedulingCoordinator.getStaffId();
        if ( staffId == null ) {
            return null;
        }
        return staffId;
    }

    private String entitySchedulingCoordinatorFullName(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }
        Staff schedulingCoordinator = appointment.getSchedulingCoordinator();
        if ( schedulingCoordinator == null ) {
            return null;
        }
        String fullName = schedulingCoordinator.getFullName();
        if ( fullName == null ) {
            return null;
        }
        return fullName;
    }
}
