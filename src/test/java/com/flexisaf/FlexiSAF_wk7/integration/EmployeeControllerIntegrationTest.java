
package com.flexisaf.FlexiSAF_wk7.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexisaf.FlexiSAF_wk7.config.TestSecurityConfig;
import com.flexisaf.FlexiSAF_wk7.entity.Employee;
import com.flexisaf.FlexiSAF_wk7.repository.EmployeeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee testEmployee;

    @BeforeEach
    public void setUp() {
        // Clean up database before each test
        employeeRepository.deleteAll();

        // Create test data
        testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setEmail("john.doe@flexisaf.com");
        testEmployee.setPhoneNumber("1234567890");
        testEmployee.setDepartment("IT");
        testEmployee.setPosition("Software Engineer");
        testEmployee.setSalary(75000.00);
        testEmployee.setDateofHire(LocalDate.of(2023, 1, 15));
        testEmployee.setStatus(Employee.EmploymentStatus.FULL_TIME);
        testEmployee.setActive(true);
        testEmployee.setAddress("123 Main St, Lagos, Nigeria");
    }

    @AfterEach
    public void tearDown() {
        // Clean up database after each test
        employeeRepository.deleteAll();
    }

    @Test
    public void testGetAllEmployees_WhenNoEmployees_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetAllEmployees_WhenEmployeesExist_ShouldReturnList() throws Exception {
        // Save test employee
        employeeRepository.save(testEmployee);

        mockMvc.perform(get("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("Doe")))
                .andExpect(jsonPath("$[0].email", is("john.doe@flexisaf.com")));
    }

    @Test
    public void testGetEmployeeById_WhenEmployeeExists_ShouldReturnEmployee() throws Exception {
        // Save test employee
        Employee savedEmployee = employeeRepository.save(testEmployee);

        mockMvc.perform(get("/api/employees/{id}", savedEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@flexisaf.com")))
                .andExpect(jsonPath("$.department", is("IT")));
    }

    @Test
    public void testGetEmployeeById_WhenEmployeeDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/employees/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateEmployee_WithValidData_ShouldReturnCreated() throws Exception {
        String employeeJson = objectMapper.writeValueAsString(testEmployee);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@flexisaf.com")));
    }

    @Test
    public void testCreateEmployee_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Create employee with missing required fields
        Employee invalidEmployee = new Employee();
        invalidEmployee.setDepartment("IT");

        String employeeJson = objectMapper.writeValueAsString(invalidEmployee);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateEmployee_WithDuplicateEmail_ShouldReturnConflict() throws Exception {
        // Save first employee
        employeeRepository.save(testEmployee);

        // Try to create another employee with same email
        Employee duplicateEmployee = new Employee();
        duplicateEmployee.setFirstName("Jane");
        duplicateEmployee.setLastName("Smith");
        duplicateEmployee.setEmail("john.doe@flexisaf.com"); // Duplicate email
        duplicateEmployee.setPhoneNumber("9876543210");
        duplicateEmployee.setDepartment("HR");
        duplicateEmployee.setPosition("HR Manager");
        duplicateEmployee.setSalary(80000.00);
        duplicateEmployee.setDateofHire(LocalDate.now());
        duplicateEmployee.setStatus(Employee.EmploymentStatus.FULL_TIME);
        duplicateEmployee.setActive(true);

        String employeeJson = objectMapper.writeValueAsString(duplicateEmployee);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isBadRequest()); // Duplicate email validation
    }

    @Test
    public void testUpdateEmployee_WithValidData_ShouldReturnUpdatedEmployee() throws Exception {
        // Save test employee
        Employee savedEmployee = employeeRepository.save(testEmployee);

        // Update employee data
        savedEmployee.setDepartment("HR");
        savedEmployee.setPosition("HR Manager");
        savedEmployee.setSalary(85000.00);

        String employeeJson = objectMapper.writeValueAsString(savedEmployee);

        mockMvc.perform(put("/api/employees/{id}", savedEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.department", is("HR")))
                .andExpect(jsonPath("$.position", is("HR Manager")))
                .andExpect(jsonPath("$.salary", is(85000.00)));
    }

    @Test
    public void testUpdateEmployee_WhenEmployeeDoesNotExist_ShouldReturn404() throws Exception {
        String employeeJson = objectMapper.writeValueAsString(testEmployee);

        mockMvc.perform(put("/api/employees/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteEmployee_WhenEmployeeExists_ShouldReturnNoContent() throws Exception {
        // Save test employee
        Employee savedEmployee = employeeRepository.save(testEmployee);

        mockMvc.perform(delete("/api/employees/{id}", savedEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify employee is deleted
        mockMvc.perform(get("/api/employees/{id}", savedEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteEmployee_WhenEmployeeDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/employees/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateMultipleEmployees_ShouldReturnCorrectCount() throws Exception {
        // Create first employee
        String employee1Json = objectMapper.writeValueAsString(testEmployee);
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employee1Json))
                .andExpect(status().isCreated());

        // Create second employee
        Employee employee2 = new Employee();
        employee2.setFirstName("Jane");
        employee2.setLastName("Smith");
        employee2.setEmail("jane.smith@flexisaf.com");
        employee2.setPhoneNumber("9876543210");
        employee2.setDepartment("Finance");
        employee2.setPosition("Accountant");
        employee2.setSalary(70000.00);
        employee2.setDateofHire(LocalDate.of(2023, 3, 1));
        employee2.setStatus(Employee.EmploymentStatus.FULL_TIME);
        employee2.setActive(true);

        String employee2Json = objectMapper.writeValueAsString(employee2);
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employee2Json))
                .andExpect(status().isCreated());

        // Verify count
        mockMvc.perform(get("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}