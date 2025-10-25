package com.flexisaf.FlexiSAF_wk7.service;

import com.flexisaf.FlexiSAF_wk7.entity.LeaveRequest;

import java.util.List;

public interface LeaveRequestService {
    LeaveRequest applyLeaveRequest(LeaveRequest leaveRequest);
    LeaveRequest getLeaveRequest(Long id);
    List<LeaveRequest> getAllLeaveRequests();
    List<LeaveRequest> getLeaveRequestsByEmployee(Long employeeId);
    LeaveRequest reviewLeaveRequest(Long id, LeaveRequest leaveRequest);
}
