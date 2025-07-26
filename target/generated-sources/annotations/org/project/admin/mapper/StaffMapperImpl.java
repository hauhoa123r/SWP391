package org.project.admin.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.project.admin.dto.request.StaffRequest;
import org.project.admin.dto.response.DepartmentResponse;
import org.project.admin.dto.response.HospitalResponse;
import org.project.admin.dto.response.StaffResponse;
import org.project.admin.entity.Department;
import org.project.admin.entity.Hospital;
import org.project.admin.entity.Staff;
import org.project.admin.entity.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-26T23:41:20+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class StaffMapperImpl implements StaffMapper {

    @Override
    public Staff toEntity(StaffRequest request) {
        if ( request == null ) {
            return null;
        }

        Staff staff = new Staff();

        staff.setUser( StaffMapper.mapUser( request.getUserId() ) );
        staff.setManager( StaffMapper.mapManager( request.getManagerId() ) );
        staff.setDepartment( StaffMapper.mapDepartment( request.getDepartmentId() ) );
        staff.setHospital( StaffMapper.mapHospital( request.getHospitalId() ) );
        staff.setAvatarUrl( request.getAvatarUrl() );
        staff.setStaffRole( request.getStaffRole() );
        staff.setFullName( request.getFullName() );
        staff.setHireDate( request.getHireDate() );
        staff.setRankLevel( request.getRankLevel() );
        staff.setStaffType( request.getStaffType() );

        return staff;
    }

    @Override
    public StaffResponse toResponse(Staff staff) {
        if ( staff == null ) {
            return null;
        }

        StaffResponse staffResponse = new StaffResponse();

        staffResponse.setUserId( staffUserUserId( staff ) );
        staffResponse.setStaffId( staff.getStaffId() );
        staffResponse.setFullName( staff.getFullName() );
        staffResponse.setStaffRole( staff.getStaffRole() );
        staffResponse.setStaffType( staff.getStaffType() );
        staffResponse.setRankLevel( staff.getRankLevel() );
        staffResponse.setHireDate( staff.getHireDate() );
        staffResponse.setDepartment( departmentToDepartmentResponse( staff.getDepartment() ) );
        staffResponse.setHospital( hospitalToHospitalResponse( staff.getHospital() ) );
        staffResponse.setAvatarUrl( staff.getAvatarUrl() );

        return staffResponse;
    }

    @Override
    public List<StaffResponse> toResponse(List<Staff> staffs) {
        if ( staffs == null ) {
            return null;
        }

        List<StaffResponse> list = new ArrayList<StaffResponse>( staffs.size() );
        for ( Staff staff : staffs ) {
            list.add( toResponse( staff ) );
        }

        return list;
    }

    private Long staffUserUserId(Staff staff) {
        if ( staff == null ) {
            return null;
        }
        User user = staff.getUser();
        if ( user == null ) {
            return null;
        }
        Long userId = user.getUserId();
        if ( userId == null ) {
            return null;
        }
        return userId;
    }

    protected DepartmentResponse departmentToDepartmentResponse(Department department) {
        if ( department == null ) {
            return null;
        }

        DepartmentResponse departmentResponse = new DepartmentResponse();

        departmentResponse.setDepartmentId( department.getDepartmentId() );
        departmentResponse.setName( department.getName() );
        departmentResponse.setDescription( department.getDescription() );
        departmentResponse.setVideoUrl( department.getVideoUrl() );
        departmentResponse.setBannerUrl( department.getBannerUrl() );
        departmentResponse.setSlogan( department.getSlogan() );

        return departmentResponse;
    }

    protected HospitalResponse hospitalToHospitalResponse(Hospital hospital) {
        if ( hospital == null ) {
            return null;
        }

        HospitalResponse hospitalResponse = new HospitalResponse();

        hospitalResponse.setHospitalId( hospital.getHospitalId() );
        hospitalResponse.setName( hospital.getName() );
        hospitalResponse.setAddress( hospital.getAddress() );
        hospitalResponse.setPhoneNumber( hospital.getPhoneNumber() );
        hospitalResponse.setEmail( hospital.getEmail() );
        hospitalResponse.setAvatarUrl( hospital.getAvatarUrl() );

        return hospitalResponse;
    }
}
