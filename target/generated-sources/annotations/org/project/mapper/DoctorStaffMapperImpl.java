package org.project.mapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.annotation.processing.Generated;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.project.entity.DepartmentEntity;
import org.project.entity.DoctorEntity;
import org.project.entity.HospitalEntity;
import org.project.entity.StaffEntity;
import org.project.entity.UserEntity;
import org.project.enums.DoctorRank;
import org.project.enums.StaffRole;
import org.project.enums.StaffType;
import org.project.model.request.DoctorStaffRequest;
import org.project.model.response.DoctorStaffResponse;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-25T22:32:21+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class DoctorStaffMapperImpl implements DoctorStaffMapper {

    private final DatatypeFactory datatypeFactory;

    public DoctorStaffMapperImpl() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch ( DatatypeConfigurationException ex ) {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public DoctorStaffResponse toResponse(StaffEntity entity) {
        if ( entity == null ) {
            return null;
        }

        DoctorStaffResponse doctorStaffResponse = new DoctorStaffResponse();

        doctorStaffResponse.setStaffId( entity.getId() );
        doctorStaffResponse.setAvatarUrl( entity.getAvatarUrl() );
        doctorStaffResponse.setUserId( entityUserEntityId( entity ) );
        doctorStaffResponse.setEmail( entityUserEntityEmail( entity ) );
        doctorStaffResponse.setPhoneNumber( entityUserEntityPhoneNumber( entity ) );
        doctorStaffResponse.setDepartmentId( entityDepartmentEntityId( entity ) );
        doctorStaffResponse.setDepartmentName( entityDepartmentEntityName( entity ) );
        doctorStaffResponse.setHospitalId( entityHospitalEntityId( entity ) );
        doctorStaffResponse.setHospitalName( entityHospitalEntityName( entity ) );
        doctorStaffResponse.setManagerId( entityManagerId( entity ) );
        doctorStaffResponse.setManagerName( entityManagerFullName( entity ) );
        DoctorRank doctorRank = entityDoctorEntityDoctorRank( entity );
        if ( doctorRank != null ) {
            doctorStaffResponse.setDoctorRank( doctorRank.name() );
        }
        doctorStaffResponse.setFullName( entity.getFullName() );
        if ( entity.getStaffRole() != null ) {
            doctorStaffResponse.setStaffRole( entity.getStaffRole().name() );
        }
        if ( entity.getStaffType() != null ) {
            doctorStaffResponse.setStaffType( entity.getStaffType().name() );
        }
        doctorStaffResponse.setRankLevel( entity.getRankLevel() );
        doctorStaffResponse.setHireDate( xmlGregorianCalendarToString( dateToXmlGregorianCalendar( entity.getHireDate() ), null ) );

        return doctorStaffResponse;
    }

    @Override
    public StaffEntity toEntity(DoctorStaffRequest request) {
        if ( request == null ) {
            return null;
        }

        StaffEntity staffEntity = new StaffEntity();

        staffEntity.setFullName( request.getFullName() );
        staffEntity.setAvatarUrl( request.getAvatarUrl() );
        staffEntity.setRankLevel( request.getRankLevel() );
        if ( request.getStaffRole() != null ) {
            staffEntity.setStaffRole( Enum.valueOf( StaffRole.class, request.getStaffRole() ) );
        }
        if ( request.getStaffType() != null ) {
            staffEntity.setStaffType( Enum.valueOf( StaffType.class, request.getStaffType() ) );
        }

        return staffEntity;
    }

    private String xmlGregorianCalendarToString( XMLGregorianCalendar xcal, String dateFormat ) {
        if ( xcal == null ) {
            return null;
        }

        if (dateFormat == null ) {
            return xcal.toString();
        }
        else {
            Date d = xcal.toGregorianCalendar().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat( dateFormat );
            return sdf.format( d );
        }
    }

    private XMLGregorianCalendar dateToXmlGregorianCalendar( Date date ) {
        if ( date == null ) {
            return null;
        }

        GregorianCalendar c = new GregorianCalendar();
        c.setTime( date );
        return datatypeFactory.newXMLGregorianCalendar( c );
    }

    private Long entityUserEntityId(StaffEntity staffEntity) {
        if ( staffEntity == null ) {
            return null;
        }
        UserEntity userEntity = staffEntity.getUserEntity();
        if ( userEntity == null ) {
            return null;
        }
        Long id = userEntity.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityUserEntityEmail(StaffEntity staffEntity) {
        if ( staffEntity == null ) {
            return null;
        }
        UserEntity userEntity = staffEntity.getUserEntity();
        if ( userEntity == null ) {
            return null;
        }
        String email = userEntity.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }

    private String entityUserEntityPhoneNumber(StaffEntity staffEntity) {
        if ( staffEntity == null ) {
            return null;
        }
        UserEntity userEntity = staffEntity.getUserEntity();
        if ( userEntity == null ) {
            return null;
        }
        String phoneNumber = userEntity.getPhoneNumber();
        if ( phoneNumber == null ) {
            return null;
        }
        return phoneNumber;
    }

    private Long entityDepartmentEntityId(StaffEntity staffEntity) {
        if ( staffEntity == null ) {
            return null;
        }
        DepartmentEntity departmentEntity = staffEntity.getDepartmentEntity();
        if ( departmentEntity == null ) {
            return null;
        }
        Long id = departmentEntity.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityDepartmentEntityName(StaffEntity staffEntity) {
        if ( staffEntity == null ) {
            return null;
        }
        DepartmentEntity departmentEntity = staffEntity.getDepartmentEntity();
        if ( departmentEntity == null ) {
            return null;
        }
        String name = departmentEntity.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Long entityHospitalEntityId(StaffEntity staffEntity) {
        if ( staffEntity == null ) {
            return null;
        }
        HospitalEntity hospitalEntity = staffEntity.getHospitalEntity();
        if ( hospitalEntity == null ) {
            return null;
        }
        Long id = hospitalEntity.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityHospitalEntityName(StaffEntity staffEntity) {
        if ( staffEntity == null ) {
            return null;
        }
        HospitalEntity hospitalEntity = staffEntity.getHospitalEntity();
        if ( hospitalEntity == null ) {
            return null;
        }
        String name = hospitalEntity.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Long entityManagerId(StaffEntity staffEntity) {
        if ( staffEntity == null ) {
            return null;
        }
        StaffEntity manager = staffEntity.getManager();
        if ( manager == null ) {
            return null;
        }
        Long id = manager.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityManagerFullName(StaffEntity staffEntity) {
        if ( staffEntity == null ) {
            return null;
        }
        StaffEntity manager = staffEntity.getManager();
        if ( manager == null ) {
            return null;
        }
        String fullName = manager.getFullName();
        if ( fullName == null ) {
            return null;
        }
        return fullName;
    }

    private DoctorRank entityDoctorEntityDoctorRank(StaffEntity staffEntity) {
        if ( staffEntity == null ) {
            return null;
        }
        DoctorEntity doctorEntity = staffEntity.getDoctorEntity();
        if ( doctorEntity == null ) {
            return null;
        }
        DoctorRank doctorRank = doctorEntity.getDoctorRank();
        if ( doctorRank == null ) {
            return null;
        }
        return doctorRank;
    }
}
