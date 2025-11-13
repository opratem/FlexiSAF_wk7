package com.flexisaf.FlexiSAF_wk7.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexisaf.FlexiSAF_wk7.entity.Employee;
import com.flexisaf.FlexiSAF_wk7.entity.LeaveRequest;
import com.flexisaf.FlexiSAF_wk7.repository.EmployeeRepository;
import com.flexisaf.FlexiSAF_wk7.repository.LeaveRequestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class LeaveRequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee testEmployee;
    private LeaveRequest testLeaveRequest;

    @BeforeEach
    public void setUp() {
        // Clean up database before each test
        leaveRequestRepository.deleteAll();
        employeeRepository.deleteAll();

        // Create test employee
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
        testEmployee = employeeRepository.save(testEmployee);

        // Create test leave request
        testLeaveRequest = new LeaveRequest();
        testLeaveRequest.setEmployee(testEmployee);
        testLeaveRequest.setStartDate(LocalDate.now().plusDays(7));
        testLeaveRequest.setEndDate(LocalDate.now().plusDays(14));
        testLeaveRequest.setLeaveType(LeaveRequest.LeaveType.ANNUAL);
        testLeaveRequest.setReason("Family vacation");
    }

    @AfterEach
    public void tearDown() {
        // Clean up database after each test
        leaveRequestRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    public void testGetAllLeaveRequests_WhenNoRequests_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetAllLeaveRequests_WhenRequestsExist_ShouldReturnList() throws Exception {
        // Save test leave request
        leaveRequestRepository.save(testLeaveRequest);

        mockMvc.perform(get("/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].leaveType", is("ANNUAL")))
                .andExpect(jsonPath("$[0].reason", is("Family vacation")));
    }

    @Test
    public void testGetLeaveRequestById_WhenRequestExists_ShouldReturnRequest() throws Exception {
        // Save test leave request
        LeaveRequest savedRequest = leaveRequestRepository.save(testLeaveRequest);

        mockMvc.perform(get("/api/leaves/{id}", savedRequest.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedRequest.getId().intValue())))
                .andExpect(jsonPath("$.leaveType", is("ANNUAL")))
                .andExpect(jsonPath("$.reason", is("Family vacation")));
    }

    @Test
    public void testGetLeaveRequestById_WhenRequestDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/leaves/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetLeaveRequestsByEmployee_WhenEmployeeExists_ShouldReturnRequests() throws Exception {
        // Save test leave request
        leaveRequestRepository.save(testLeaveRequest);

        mockMvc.perform(get("/api/leaves/employees/{employeeId}", testEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].leaveType", is("ANNUAL")));
    }

    @Test
    public void testGetLeaveRequestsByEmployee_WhenEmployeeDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/leaves/employees/{employeeId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testApplyLeaveRequest_WithValidData_ShouldReturnCreated() throws Exception {
        String leaveRequestJson = objectMapper.writeValueAsString(testLeaveRequest);

        mockMvc.perform(post("/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leaveRequestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.leaveType", is("ANNUAL")))
                .andExpect(jsonPath("$.dateApplied", notNullValue()));
    }

    @Test
    public void testApplyLeaveRequest_WithoutEmployee_ShouldReturnBadRequest() throws Exception {
        testLeaveRequest.setEmployee(null);
        String leaveRequestJson = objectMapper.writeValueAsString(testLeaveRequest);

        mockMvc.perform(post("/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leaveRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testApplyLeaveRequest_WithNonExistentEmployee_ShouldReturn404() throws Exception {
        Employee nonExistentEmployee = new Employee();
        nonExistentEmployee.setId(999L);
        testLeaveRequest.setEmployee(nonExistentEmployee);

        String leaveRequestJson = objectMapper.writeValueAsString(testLeaveRequest);

        mockMvc.perform(post("/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leaveRequestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCancelLeaveRequest_WhenRequestExists_ShouldReturnNoContent() throws Exception {
        // Save test leave request
        LeaveRequest savedRequest = leaveRequestRepository.save(testLeaveRequest);

        mockMvc.perform(delete("/api/leaves/{id}", savedRequest.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify status is CANCELLED
        LeaveRequest cancelledRequest = leaveRequestRepository.findById(savedRequest.getId()).orElse(null);
        assert cancelledRequest != null;
        assert cancelledRequest.getStatus() == LeaveRequest.LeaveStatus.CANCELLED;
    }

    @Test
    public void testCancelLeaveRequest_WhenRequestDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/leaves/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testApplyMultipleLeaveRequests_ForSameEmployee_ShouldReturnCorrectCount() throws Exception {
        // Create first leave request
        String request1Json = objectMapper.writeValueAsString(testLeaveRequest);
        mockMvc.perform(post("/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request1Json))
                .andExpect(status().isCreated());

        // Create second leave request
        LeaveRequest request2 = new LeaveRequest();
        request2.setEmployee(testEmployee);
        request2.setStartDate(LocalDate.now().plusDays(30));
        request2.setEndDate(LocalDate.now().plusDays(35));
        request2.setLeaveType(LeaveRequest.LeaveType.SICK);
        request2.setReason("Medical appointment");

        String request2Json = objectMapper.writeValueAsString(request2);
        mockMvc.perform(post("/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request2Json))
                .andExpect(status().isCreated());

        // Verify count for employee
        mockMvc.perform(get("/api/leaves/employees/{employeeId}", testEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testApplyLeaveRequest_WithDifferentLeaveTypes_ShouldSucceed() throws Exception {
        LeaveRequest.LeaveType[] leaveTypes = LeaveRequest.LeaveType.values();

        for (int i = 0; i < leaveTypes.length; i++) {
            LeaveRequest request = new LeaveRequest();
            request.setEmployee(testEmployee);
            request.setStartDate(LocalDate.now().plusDays(7 + (i * 10)));
            request.setEndDate(LocalDate.now().plusDays(14 + (i * 10)));
            request.setLeaveType(leaveTypes[i]);
            request.setReason("Testing " + leaveTypes[i]);

            String requestJson = objectMapper.writeValueAsString(request);
            mockMvc.perform(post("/api/leaves")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.leaveType", is(leaveTypes[i].toString())));
        }

        // Verify all requests were created
        mockMvc.perform(get("/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(leaveTypes.length)));
    }

    @Test
    public void testLeaveRequestLifecycle_CreateAndCancel_ShouldWork() throws Exception {
        // Create leave request
        String requestJson = objectMapper.writeValueAsString(testLeaveRequest);
        String response = mockMvc.perform(post("/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        LeaveRequest createdRequest = objectMapper.readValue(response, LeaveRequest.class);

        // Cancel the request
        mockMvc.perform(delete("/api/leaves/{id}", createdRequest.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify status changed to CANCELLED
        mockMvc.perform(get("/api/leaves/{id}", createdRequest.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }
}
