package org.project.repository;

import org.project.entity.LeaveRequestEntity;
import org.project.enums.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequestEntity, Long> {

    @Query("""
            SELECT r 
            FROM LeaveRequestEntity r
            WHERE r.approvedBy.id = :managerId
                  AND r.startDate > CURRENT_TIMESTAMP
          """)
    Page<LeaveRequestEntity> findFutureLeaveRequestByManager(
            @Param("managerId") Long managerId,
            Pageable pageable
    );

    Long countByApprovedByManager_IdAndStartDateAfter(
            Long managerId,
            Timestamp now
    );

    Long countByApprovedByManager_IdAndStartDateAfterAndStatus(
            Long managerId,
            Timestamp now,
            LeaveStatus status
    );

    List<LeaveRequestEntity> findTop5ByStaffEntity_IdOrderByCreatedAtDesc(Long staffId);

    Page<LeaveRequestEntity> findAllByStaffEntity_IdOrderByCreatedAtDesc(Long staffId, Pageable pageable);

    @Query("UPDATE LeaveRequestEntity r SET r.status = :status, r.approvedBy.id = :approvedById WHERE r.id = :leaveRequestId")
    int updateLeaveRequestStatus(@Param("leaveRequestId") Long leaveRequestId,
                                 @Param("status") LeaveStatus status,
                                 @Param("approvedById") Long approvedById);


}
