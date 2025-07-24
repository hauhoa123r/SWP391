package org.project.admin.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.project.admin.dto.request.StaffScheduleRequest;
import org.project.admin.dto.response.StaffScheduleResponse;
import org.project.admin.entity.Staff;
import org.project.admin.entity.StaffSchedule;
import org.project.admin.enums.staffs.StaffRole;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-24T11:18:08+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class StaffScheduleMapperImpl implements StaffScheduleMapper {

    @Override
    public StaffSchedule toEntity(StaffScheduleRequest request) {
        if ( request == null ) {
            return null;
        }

        StaffSchedule staffSchedule = new StaffSchedule();

        staffSchedule.setAvailableDate( request.getAvailableDate() );
        staffSchedule.setStartTime( request.getStartTime() );
        staffSchedule.setEndTime( request.getEndTime() );

        return staffSchedule;
    }

    @Override
    public StaffScheduleResponse toResponse(StaffSchedule staffSchedule) {
        if ( staffSchedule == null ) {
            return null;
        }

        StaffScheduleResponse staffScheduleResponse = new StaffScheduleResponse();

        staffScheduleResponse.setStaffId( staffScheduleStaffStaffId( staffSchedule ) );
        staffScheduleResponse.setStaffFullName( staffScheduleStaffFullName( staffSchedule ) );
        staffScheduleResponse.setStaffAvatarUrl( staffScheduleStaffAvatarUrl( staffSchedule ) );
        StaffRole staffRole = staffScheduleStaffStaffRole( staffSchedule );
        if ( staffRole != null ) {
            staffScheduleResponse.setStaffRole( staffRole.name() );
        }
        staffScheduleResponse.setStaffScheduleId( staffSchedule.getStaffScheduleId() );
        staffScheduleResponse.setAvailableDate( staffSchedule.getAvailableDate() );
        staffScheduleResponse.setStartTime( staffSchedule.getStartTime() );
        staffScheduleResponse.setEndTime( staffSchedule.getEndTime() );

        return staffScheduleResponse;
    }

    @Override
    public List<StaffScheduleResponse> toResponseList(List<StaffSchedule> schedules) {
        if ( schedules == null ) {
            return null;
        }

        List<StaffScheduleResponse> list = new ArrayList<StaffScheduleResponse>( schedules.size() );
        for ( StaffSchedule staffSchedule : schedules ) {
            list.add( toResponse( staffSchedule ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromRequest(StaffScheduleRequest request, StaffSchedule entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getAvailableDate() != null ) {
            entity.setAvailableDate( request.getAvailableDate() );
        }
        if ( request.getStartTime() != null ) {
            entity.setStartTime( request.getStartTime() );
        }
        if ( request.getEndTime() != null ) {
            entity.setEndTime( request.getEndTime() );
        }
    }

    private Long staffScheduleStaffStaffId(StaffSchedule staffSchedule) {
        if ( staffSchedule == null ) {
            return null;
        }
        Staff staff = staffSchedule.getStaff();
        if ( staff == null ) {
            return null;
        }
        Long staffId = staff.getStaffId();
        if ( staffId == null ) {
            return null;
        }
        return staffId;
    }

    private String staffScheduleStaffFullName(StaffSchedule staffSchedule) {
        if ( staffSchedule == null ) {
            return null;
        }
        Staff staff = staffSchedule.getStaff();
        if ( staff == null ) {
            return null;
        }
        String fullName = staff.getFullName();
        if ( fullName == null ) {
            return null;
        }
        return fullName;
    }

    private String staffScheduleStaffAvatarUrl(StaffSchedule staffSchedule) {
        if ( staffSchedule == null ) {
            return null;
        }
        Staff staff = staffSchedule.getStaff();
        if ( staff == null ) {
            return null;
        }
        String avatarUrl = staff.getAvatarUrl();
        if ( avatarUrl == null ) {
            return null;
        }
        return avatarUrl;
    }

    private StaffRole staffScheduleStaffStaffRole(StaffSchedule staffSchedule) {
        if ( staffSchedule == null ) {
            return null;
        }
        Staff staff = staffSchedule.getStaff();
        if ( staff == null ) {
            return null;
        }
        StaffRole staffRole = staff.getStaffRole();
        if ( staffRole == null ) {
            return null;
        }
        return staffRole;
    }
}
