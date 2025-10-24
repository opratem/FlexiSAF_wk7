package com.flexisaf.FlexiSAF_wk7.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
@Data
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leavetype;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    @Column(length = 500)
    private String reason;

    @Column(length = 500)
    private String managerComment;

    private LocalDate dateApplied;

    private LocalDate dateReviewed;

    private enum LeaveType{
        CASUAL, SICK, MATERNITY, ANNUAL, STUDY
    }

    private enum LeaveStatus{
        PENDING, APPROVED, REJECTED, CANCELLED
    }

}
