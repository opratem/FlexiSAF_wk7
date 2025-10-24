package com.flexisaf.FlexiSAF_wk7.controller;

import com.flexisaf.FlexiSAF_wk7.entity.LeaveRequest;
import com.flexisaf.FlexiSAF_wk7.service.EmployeeService;
import com.flexisaf.FlexiSAF_wk7.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;
    private final EmployeeService employeeService;

    public LeaveRequestController(LeaveRequestService leaveRequestService, EmployeeService employeeService) {
        this.leaveRequestService = leaveRequestService;
        this.employeeService = employeeService;
    }
    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getAllLeaveRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequest> getLeaveRequest(@PathVariable Long id) {
        LeaveRequest leaveRequest = leaveRequestService.getLeaveRequest(id);
        return ResponseEntity.ok(leaveRequest);
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<List<LeaveRequest>> getLeaveRequestsByEmployee(@PathVariable Long employeeId) {
        employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(leaveRequestService.getLeaveRequestsByEmployee(employeeId));
    }

    @PostMapping
    public ResponseEntity<LeaveRequest> applyLeaveRequest(@Valid @RequestBody LeaveRequest leaveRequest) {
        Long empId = leaveRequest.getEmployee() != null ? leaveRequest.getEmployee().getId() : null;
        if (empId == null) {
            return ResponseEntity.badRequest().build();
        }
        employeeService.getEmployeeById(empId);
        LeaveRequest createdLeaveRequest = leaveRequestService.applyLeaveRequest(leaveRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLeaveRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelorDeleteLeaveRequest(@PathVariable Long id) {
        LeaveRequest leaveRequest = leaveRequestService.getLeaveRequest(id);
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.CANCELLED);
        leaveRequestService.reviewLeaveRequest(id, leaveRequest);
        return ResponseEntity.noContent().build();
    }


}
