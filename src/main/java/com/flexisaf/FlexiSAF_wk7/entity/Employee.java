package com.flexisaf.FlexiSAF_wk7.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "employees")
@Data
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false,length=50)
    private String firstName;

    @Column(nullable=false,length=50)
    private String lastName;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(length=20)
    private String phoneNumber;

    private String department;

    private String position;

    private double salary;

    private LocalDate dateofHire;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus status;

    private boolean active;

    @Lob
    private String address;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<LeaveRequest> leaveRequests;

    public enum EmploymentStatus {
        FULL_TIME, PART_TIME, CONTRACT;
    }

}
