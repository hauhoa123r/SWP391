package org.project.admin.dto.response;

import lombok.Data;
import org.project.admin.enums.AuditAction;

import java.time.LocalDateTime;

@Data
public class StaffAuditLogResponse {
    private Long auditId;
    private AuditAction action;
    private String oldData;
    private String newData;
    private Long changedBy;
    private LocalDateTime changedAt;
}
