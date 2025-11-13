package com.flexisaf.FlexiSAF_wk7.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
@Data
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull(message = "Employee is required")
    @JsonIgnoreProperties("leaveRequests")
    private Employee employee;

    @Column(nullable = false)
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @Column(nullable = false)
    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Leave type is required")
    private LeaveType leaveType;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    @Column(length = 500)
    private String reason;

    @Column(length = 500)
    private String managerComment;

    private LocalDate dateApplied;

    private LocalDate dateReviewed;

    public enum LeaveType {
        CASUAL, SICK, MATERNITY, ANNUAL, STUDY
    }

    public enum LeaveStatus {
        PENDING, APPROVED, REJECTED, CANCELLED
    }
}
