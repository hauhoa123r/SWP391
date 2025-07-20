package org.project.api;

import org.project.exception.LeaveLeftNotEnoughException;
import org.project.model.dto.LeaveRequestDTO;
import org.project.model.response.LeaveRequestResponse;
import org.project.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-request")
public class LeaveRequestAPI {

    private LeaveRequestService leaveRequestService;

    @Autowired
    public void setLeaveRequestService(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @PostMapping("create")
    public ResponseEntity<String> createLeaveRequest(@RequestBody LeaveRequestDTO leaveRequestDTO) {
        try {
            boolean ok = leaveRequestService.saveLeaveRequest(leaveRequestDTO);
            if (ok) {
                return ResponseEntity.ok("Đơn đã được gửi!");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể lưu đơn nghỉ");
            }
        } catch (LeaveLeftNotEnoughException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không đủ ngày phép!");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy nhân viên");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi máy chủ!");
        }
    }

    @GetMapping("get-modal/{requestId}")
    public LeaveRequestResponse getLeaveRequest(@PathVariable("requestId") Long requestId) {
        return leaveRequestService.getLeaveRequestById(requestId);
    }
}
