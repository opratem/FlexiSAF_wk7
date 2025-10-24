package com.flexisaf.FlexiSAF_wk7.service.impl;

import com.flexisaf.FlexiSAF_wk7.entity.Employee;
import com.flexisaf.FlexiSAF_wk7.repository.EmployeeRepository;
import com.flexisaf.FlexiSAF_wk7.service.EmployeeService;
import org.springframework.data.annotation.Reference;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee createEmployee(Employee employee) {
        if(employeeRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Employee already exists");
        }
        return employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id);
    }

    @Override
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Employee employee = getEmployeeById(id);

        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setPhoneNumber(employeeDetails.getPhoneNumber());
        employee.setDepartment(employeeDetails.getDepartment());
        employee.setPosition(employeeDetails.getPosition());
        employee.setSalary(employeeDetails.getSalary());
        employee.setStatus(employeeDetails.getStatus());
        employee.setActive(employeeDetails.isActive());
        employee.setAddress(employeeDetails.getAddress());

        return employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
    }
}
