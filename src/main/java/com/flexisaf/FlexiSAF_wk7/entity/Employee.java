package com.flexisaf.FlexiSAF_wk7.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "First name is required")
    @Column(nullable=false,length=50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(nullable=false,length=50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
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
    @JsonIgnoreProperties("employee")
    private List<LeaveRequest> leaveRequests;

    public enum EmploymentStatus {
        FULL_TIME, PART_TIME, CONTRACT;
    }

}