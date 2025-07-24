package org.project.repository;

import jakarta.transaction.Transactional;
import org.project.entity.LeaveRequestEntity;
import org.project.enums.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Transactional
    @Query("UPDATE LeaveRequestEntity r SET r.status = :status, r.rejectionReason =:rejectReason WHERE r.id = :leaveRequestId")
    int updateLeaveRequestStatus(@Param("leaveRequestId") Long leaveRequestId,
                                 @Param("status") LeaveStatus status,
                                 @Param("rejectReason")String rejectReason
                                );

}
