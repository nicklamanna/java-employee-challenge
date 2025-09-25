package test.java.com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private ObjectMapper objectMapper;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(objectMapper);
        try {
            var restClientField = EmployeeService.class.getDeclaredField("restClient");
            restClientField.setAccessible(true);
            restClientField.set(employeeService, restClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllEmployees_Success() {
        // Given
        Map<String, Object> mockResponse = Map.of(
                "data",
                List.of(
                        Map.of(
                                "id",
                                "1",
                                "employee_name",
                                "Nick LaManna",
                                "employee_salary",
                                50000,
                                "employee_age",
                                30),
                        Map.of(
                                "id",
                                "2",
                                "employee_name",
                                "Jane Smith",
                                "employee_salary",
                                60000,
                                "employee_age",
                                25)));

        Employee employee1 = new Employee();
        employee1.setId("1");
        employee1.setEmployee_name("Nick LaManna");
        employee1.setEmployee_salary(50000);
        employee1.setEmployee_age(30);

        Employee employee2 = new Employee();
        employee2.setId("2");
        employee2.setEmployee_name("Jane Smith");
        employee2.setEmployee_salary(60000);
        employee2.setEmployee_age(25);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/employee")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        when(objectMapper.convertValue(any(Map.class), eq(Employee.class)))
                .thenReturn(employee1)
                .thenReturn(employee2);

        // When
        List<Employee> result = employeeService.getAllEmployees();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Nick LaManna", result.get(0).getEmployee_name());
        assertEquals("Jane Smith", result.get(1).getEmployee_name());
    }

    @Test
    void getAllEmployees_EmptyResponse() {
        // Given
        Map<String, Object> mockResponse = Map.of();

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/employee")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        // When
        List<Employee> result = employeeService.getAllEmployees();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getEmployeeById_Success() {
        // Given
        String employeeId = "123";
        Map<String, Object> mockResponse =
                Map.of("data", Map.of("id", employeeId, "employee_name", "Nick LaManna", "employee_salary", 50000));

        Employee mockEmployee = new Employee();
        mockEmployee.setId(employeeId);
        mockEmployee.setEmployee_name("Nick LaManna");
        mockEmployee.setEmployee_salary(50000);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/employee/{id}", employeeId)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);
        when(objectMapper.convertValue(any(Map.class), eq(Employee.class))).thenReturn(mockEmployee);

        // When
        Employee result = employeeService.getEmployeeById(employeeId);

        // Then
        assertNotNull(result);
        assertEquals(employeeId, result.getId());
        assertEquals("Nick LaManna", result.getEmployee_name());
    }

    @Test
    void getEmployeeById_NotFound() {
        // Given
        String employeeId = "999";
        Map<String, Object> mockResponse = Map.of();

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/employee/{id}", employeeId)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        // When
        Employee result = employeeService.getEmployeeById(employeeId);

        // Then
        assertNull(result);
    }

    @Test
    void getHighestSalaryOfEmployees_Success() {
        // Given
        Map<String, Object> mockResponse = Map.of(
                "data",
                List.of(
                        Map.of("employee_salary", 50000),
                        Map.of("employee_salary", 75000),
                        Map.of("employee_salary", 60000)));

        Employee emp1 = new Employee();
        emp1.setEmployee_salary(50000);
        Employee emp2 = new Employee();
        emp2.setEmployee_salary(75000);
        Employee emp3 = new Employee();
        emp3.setEmployee_salary(60000);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/employee")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);
        when(objectMapper.convertValue(any(Map.class), eq(Employee.class)))
                .thenReturn(emp1)
                .thenReturn(emp2)
                .thenReturn(emp3);

        // When
        Integer result = employeeService.getHighestSalaryOfEmployees();

        // Then
        assertEquals(75000, result);
    }

    @Test
    void createEmployee_Success() {
        // Given
        EmployeeInput input = new EmployeeInput();
        input.setName("Nick LaManna");
        input.setSalary(50000);
        input.setAge(30);
        input.setTitle("Developer");

        Map<String, Object> mockResponse =
                Map.of("data", Map.of("id", "123", "employee_name", "Nick LaManna", "employee_salary", 50000));

        Employee mockEmployee = new Employee();
        mockEmployee.setId("123");
        mockEmployee.setEmployee_name("Nick LaManna");
        mockEmployee.setEmployee_salary(50000);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/employee")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(input)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);
        when(objectMapper.convertValue(any(Map.class), eq(Employee.class))).thenReturn(mockEmployee);

        // When
        Employee result = employeeService.createEmployee(input);

        // Then
        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals("Nick LaManna", result.getEmployee_name());
    }

    @Test
    void getEmployeesByNameSearch_Success() {
        // Given
        String searchString = "Nick";
        Map<String, Object> mockResponse = Map.of(
                "data",
                List.of(
                        Map.of("id", "1", "employee_name", "Nick LaManna"),
                        Map.of("id", "2", "employee_name", "Jane Smith"),
                        Map.of("id", "3", "employee_name", "Nicky Rodriguez")));

        Employee emp1 = new Employee();
        emp1.setEmployee_name("Nick LaManna");
        Employee emp2 = new Employee();
        emp2.setEmployee_name("Jane Smith");
        Employee emp3 = new Employee();
        emp3.setEmployee_name("Nicky Rodriguez");

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/employee")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);
        when(objectMapper.convertValue(any(Map.class), eq(Employee.class)))
                .thenReturn(emp1)
                .thenReturn(emp2)
                .thenReturn(emp3);

        // When
        List<Employee> result = employeeService.getEmployeesByNameSearch(searchString);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(emp -> emp.getEmployee_name().equals("Nick LaManna")));
        assertTrue(result.stream().anyMatch(emp -> emp.getEmployee_name().equals("Nicky Rodriguez")));
        assertFalse(result.stream().anyMatch(emp -> emp.getEmployee_name().equals("Jane Smith")));
    }
}
