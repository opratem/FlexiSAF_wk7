package com.flexisaf.FlexiSAF_wk7.service.impl;

import com.flexisaf.FlexiSAF_wk7.entity.LeaveRequest;
import com.flexisaf.FlexiSAF_wk7.exception.ResourceNotFoundException;
import com.flexisaf.FlexiSAF_wk7.repository.LeaveRequestRepository;
import com.flexisaf.FlexiSAF_wk7.service.LeaveRequestService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveRequestServiceImpl(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Override
    public LeaveRequest applyLeaveRequest(LeaveRequest leaveRequest) {
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.PENDING);
        leaveRequest.setDateApplied(LocalDate.now());
        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    public LeaveRequest getLeaveRequest(Long id){
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " +id));
    }

    @Override
    public List<LeaveRequest> getAllLeaveRequests(){
        return leaveRequestRepository.findAll();
    }

    @Override
    public List<LeaveRequest> getLeaveRequestsByEmployee(Long employeeId){
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    @Override
    public LeaveRequest reviewLeaveRequest(Long id, LeaveRequest updatedLeaveRequest) {
        LeaveRequest leaveRequest = getLeaveRequest(id);
        leaveRequest.setStatus(updatedLeaveRequest.getStatus());
        leaveRequest.setManagerComment(updatedLeaveRequest.getManagerComment());
        leaveRequest.setDateReviewed(LocalDate.now());
        return leaveRequestRepository.save(leaveRequest);
    }
}
