package test.java.com.reliaquest.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.ApiApplication;
import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
@ContextConfiguration(classes = {ApiApplication.class, EmployeeController.class})
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllEmployees_Success() throws Exception {
        // Given
        Employee emp1 = new Employee();
        emp1.setId("1");
        emp1.setEmployee_name("John Doe");
        emp1.setEmployee_salary(50000);
        emp1.setEmployee_age(30);

        Employee emp2 = new Employee();
        emp2.setId("2");
        emp2.setEmployee_name("Jane Smith");
        emp2.setEmployee_salary(60000);
        emp2.setEmployee_age(25);

        List<Employee> employees = Arrays.asList(emp1, emp2);
        when(employeeService.getAllEmployees()).thenReturn(employees);

        // When & Then
        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].employee_name").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].employee_name").value("Jane Smith"));
    }

    @Test
    void getEmployeeById_Success() throws Exception {
        // Given
        String employeeId = "123";
        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setEmployee_name("John Doe");
        employee.setEmployee_salary(50000);
        employee.setEmployee_age(30);

        when(employeeService.getEmployeeById(employeeId)).thenReturn(employee);

        // When & Then
        mockMvc.perform(get("/api/v1/employee/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(employeeId))
                .andExpect(jsonPath("$.employee_name").value("John Doe"))
                .andExpect(jsonPath("$.employee_salary").value(50000));
    }

    @Test
    void getEmployeesByNameSearch_Success() throws Exception {
        // Given
        String searchString = "John";
        Employee emp1 = new Employee();
        emp1.setId("1");
        emp1.setEmployee_name("John Doe");

        Employee emp2 = new Employee();
        emp2.setId("2");
        emp2.setEmployee_name("Johnny Cash");

        List<Employee> employees = Arrays.asList(emp1, emp2);
        when(employeeService.getEmployeesByNameSearch(searchString)).thenReturn(employees);

        // When & Then
        mockMvc.perform(get("/api/v1/employee/search/{searchString}", searchString))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].employee_name").value("John Doe"))
                .andExpect(jsonPath("$[1].employee_name").value("Johnny Cash"));
    }

    @Test
    void getHighestSalaryOfEmployees_Success() throws Exception {
        // Given
        Integer highestSalary = 75000;
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(highestSalary);

        // When & Then
        mockMvc.perform(get("/api/v1/employee/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("75000"));
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_Success() throws Exception {
        // Given
        List<String> topEarners = Arrays.asList("John Doe", "Jane Smith", "Bob Johnson");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(topEarners);

        // When & Then
        mockMvc.perform(get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value("John Doe"))
                .andExpect(jsonPath("$[1]").value("Jane Smith"))
                .andExpect(jsonPath("$[2]").value("Bob Johnson"));
    }

    @Test
    void createEmployee_Success() throws Exception {
        // Given
        EmployeeInput input = new EmployeeInput();
        input.setName("John Doe");
        input.setSalary(50000);
        input.setAge(30);
        input.setTitle("Developer");

        Employee createdEmployee = new Employee();
        createdEmployee.setId("123");
        createdEmployee.setEmployee_name("John Doe");
        createdEmployee.setEmployee_salary(50000);
        createdEmployee.setEmployee_age(30);

        when(employeeService.createEmployee(any(EmployeeInput.class))).thenReturn(createdEmployee);

        // When & Then
        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.employee_name").value("John Doe"))
                .andExpect(jsonPath("$.employee_salary").value(50000));
    }

    @Test
    void deleteEmployeeById_Success() throws Exception {
        // Given
        String employeeId = "123";
        String deleteResponse = "Nick LaManna";
        when(employeeService.deleteEmployeeById(employeeId)).thenReturn(deleteResponse);

        // When & Then
        mockMvc.perform(delete("/api/v1/employee/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string("Nick LaManna"));
    }

    @Test
    void deleteEmployeeById_NotFound() throws Exception {
        // Given
        String employeeId = "999";
        String deleteResponse = "Employee not found";
        when(employeeService.deleteEmployeeById(employeeId)).thenReturn(deleteResponse);

        // When & Then
        mockMvc.perform(delete("/api/v1/employee/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string("Employee not found"));
    }

    @Test
    void getAllEmployees_EmptyList() throws Exception {
        // Given
        when(employeeService.getAllEmployees()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}