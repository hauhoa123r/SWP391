package org.project.model.response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.annotation.processing.Generated;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.project.entity.DepartmentEntity;
import org.project.entity.HospitalEntity;
import org.project.entity.StaffEntity;
import org.project.entity.UserEntity;
import org.project.enums.StaffRole;
import org.project.enums.StaffType;
import org.project.model.request.AdminStaffUpdateRequest;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-24T11:18:08+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class AdminStaffMapperImpl implements AdminStaffMapper {

    private final DatatypeFactory datatypeFactory;

    public AdminStaffMapperImpl() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch ( DatatypeConfigurationException ex ) {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public AdminStaffResponse toResponse(StaffEntity staffEntity) {
        if ( staffEntity == null ) {
            return null;
        }

        AdminStaffResponse.AdminStaffResponseBuilder adminStaffResponse = AdminStaffResponse.builder();

        adminStaffResponse.id( staffEntity.getId() );
        adminStaffResponse.fullName( staffEntity.getFullName() );
        adminStaffResponse.email( staffEntityUserEntityEmail( staffEntity ) );
        adminStaffResponse.departmentName( staffEntityDepartmentEntityName( staffEntity ) );
        adminStaffResponse.hospitalName( staffEntityHospitalEntityName( staffEntity ) );
        if ( staffEntity.getStaffRole() != null ) {
            adminStaffResponse.staffRole( staffEntity.getStaffRole().name() );
        }
        if ( staffEntity.getStaffType() != null ) {
            adminStaffResponse.staffType( staffEntity.getStaffType().name() );
        }
        adminStaffResponse.rankLevel( staffEntity.getRankLevel() );
        adminStaffResponse.status( staffEntity.getStaffStatus() );

        adminStaffResponse.managerName( staffEntity.getManager() != null ? staffEntity.getManager().getFullName() : null );
        adminStaffResponse.averageRating( staffEntity.getAverageRating() );
        adminStaffResponse.reviewCount( staffEntity.getReviewCount() );

        return adminStaffResponse.build();
    }

    @Override
    public AdminStaffDetailResponse toDetailResponse(StaffEntity staff) {
        if ( staff == null ) {
            return null;
        }

        AdminStaffDetailResponse.AdminStaffDetailResponseBuilder adminStaffDetailResponse = AdminStaffDetailResponse.builder();

        adminStaffDetailResponse.id( staff.getId() );
        adminStaffDetailResponse.fullName( staff.getFullName() );
        adminStaffDetailResponse.email( staffEntityUserEntityEmail( staff ) );
        adminStaffDetailResponse.phoneNumber( staffUserEntityPhoneNumber( staff ) );
        adminStaffDetailResponse.departmentName( staffEntityDepartmentEntityName( staff ) );
        adminStaffDetailResponse.hospitalName( staffEntityHospitalEntityName( staff ) );
        if ( staff.getStaffRole() != null ) {
            adminStaffDetailResponse.staffRole( staff.getStaffRole().name() );
        }
        if ( staff.getStaffType() != null ) {
            adminStaffDetailResponse.staffType( staff.getStaffType().name() );
        }
        adminStaffDetailResponse.rankLevel( staff.getRankLevel() );
        adminStaffDetailResponse.avatarUrl( staff.getAvatarUrl() );
        adminStaffDetailResponse.hireDate( xmlGregorianCalendarToString( dateToXmlGregorianCalendar( staff.getHireDate() ), null ) );

        adminStaffDetailResponse.managerName( staff.getManager() != null ? staff.getManager().getFullName() : null );
        adminStaffDetailResponse.averageRating( staff.getAverageRating() );
        adminStaffDetailResponse.reviewCount( staff.getReviewCount() );

        return adminStaffDetailResponse.build();
    }

    @Override
    public void updateStaffFromRequest(AdminStaffUpdateRequest request, StaffEntity entity) {
        if ( request == null ) {
            return;
        }

        entity.setFullName( request.getFullName() );
        entity.setAvatarUrl( request.getAvatarUrl() );
        entity.setRankLevel( request.getRankLevel() );
        if ( request.getStaffRole() != null ) {
            entity.setStaffRole( Enum.valueOf( StaffRole.class, request.getStaffRole() ) );
        }
        else {
            entity.setStaffRole( null );
        }
        if ( request.getStaffType() != null ) {
            entity.setStaffType( Enum.valueOf( StaffType.class, request.getStaffType() ) );
        }
        else {
            entity.setStaffType( null );
        }
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

    private String staffEntityUserEntityEmail(StaffEntity staffEntity) {
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

    private String staffEntityDepartmentEntityName(StaffEntity staffEntity) {
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

    private String staffEntityHospitalEntityName(StaffEntity staffEntity) {
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

    private String staffUserEntityPhoneNumber(StaffEntity staffEntity) {
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
}
