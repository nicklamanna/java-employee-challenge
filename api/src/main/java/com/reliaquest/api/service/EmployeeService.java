package com.reliaquest.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class EmployeeService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public EmployeeService(ObjectMapper objectMapper) {
        this.restClient = RestClient.create("http://localhost:8112/api/v1");
        this.objectMapper = objectMapper;
    }

    public List<Employee> getAllEmployees() {
        log.debug("Fetching all employees");

        try {
            Map<String, Object> response = restClient
                    .get()
                    .uri("/employee")
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> employeeData = (List<Map<String, Object>>) response.get("data");
                
                List<Employee> employees = employeeData.stream()
                        .map(data -> objectMapper.convertValue(data, Employee.class))
                        .toList();
                        
                log.debug("Successfully fetched {} employees", employees.size());
                return employees;
            }
            
            log.warn("No data found in response");
            return List.of();
        } catch (Exception e) {
            log.error("Error fetching employees: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch employees", e);
        }
    }

    public List<Employee> getEmployeesByNameSearch(String searchString) {
        log.debug("Searching employees by name: {}", searchString);
        
        try {
            Map<String, Object> response = restClient
                    .get()
                    .uri("/employee")
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> employeeData = (List<Map<String, Object>>) response.get("data");
                
                List<Employee> allEmployees = employeeData.stream()
                        .map(data -> objectMapper.convertValue(data, Employee.class))
                        .toList();
                
                List<Employee> filteredEmployees = new ArrayList<>();
                String lowerSearchString = searchString.toLowerCase();
                
                for (Employee emp : allEmployees) {
                    if (emp.getEmployee_name() != null) {
                        String employeeName = emp.getEmployee_name().toLowerCase();
                        if (employeeName.contains(lowerSearchString)) {
                            filteredEmployees.add(emp);
                        }
                    }
                }
                
                log.debug("Found {} employees matching search '{}'", filteredEmployees.size(), searchString);
                return filteredEmployees;
            }
            
            log.warn("No data found in response");
            return List.of();
        } catch (Exception e) {
            log.error("Error searching employees by name: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search employees", e);
        }
    }

    public Employee getEmployeeById(String id) {
        log.debug("Fetching employee by id: {}", id);
        
        try {
            Map<String, Object> response = restClient
                    .get()
                    .uri("/employee/{id}", id)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> employeeData = (Map<String, Object>) response.get("data");
                Employee employee = objectMapper.convertValue(employeeData, Employee.class);
                log.debug("Successfully fetched employee with id: {}", id);
                return employee;
            }
            
            log.warn("No employee found with id: {}", id);
            return null;
        } catch (Exception e) {
            log.error("Error fetching employee by id {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    public Integer getHighestSalaryOfEmployees() {
        log.debug("Fetching highest salary");
        
        try {
            Map<String, Object> response = restClient
                    .get()
                    .uri("/employee")
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> employeeData = (List<Map<String, Object>>) response.get("data");
                
                List<Employee> employees = employeeData.stream()
                        .map(data -> objectMapper.convertValue(data, Employee.class))
                        .toList();
                
                Integer highestSalary = employees.stream()
                        .filter(emp -> emp.getEmployee_salary() != null)
                        .mapToInt(Employee::getEmployee_salary)
                        .max()
                        .orElse(0);
                
                log.debug("Highest salary found: {}", highestSalary);
                return highestSalary;
            }
            
            log.warn("No data found in response");
            return 0;
        } catch (Exception e) {
            log.error("Error fetching highest salary: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch highest salary", e);
        }
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        log.debug("Fetching top 10 highest earning employees");
        
        try {
            Map<String, Object> response = restClient
                    .get()
                    .uri("/employee")
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> employeeData = (List<Map<String, Object>>) response.get("data");
                
                List<Employee> employees = employeeData.stream()
                        .map(data -> objectMapper.convertValue(data, Employee.class))
                        .toList();
                
                List<Employee> validEmployees = new ArrayList<>();
                for (Employee emp : employees) {
                    if (emp.getEmployee_salary() != null) {
                        validEmployees.add(emp);
                    }
                }
                
                validEmployees.sort((e1, e2) -> e2.getEmployee_salary() - e1.getEmployee_salary());
                
                List<String> topTenNames = new ArrayList<>();
                int count = Math.min(10, validEmployees.size());
                for (int i = 0; i < count; i++) {
                    topTenNames.add(validEmployees.get(i).getEmployee_name());
                }
                
                log.debug("Found top {} highest earning employees", topTenNames.size());
                return topTenNames;
            }
            
            log.warn("No data found in response");
            return List.of();
        } catch (Exception e) {
            log.error("Error fetching top 10 highest earning employees: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch top 10 highest earning employees", e);
        }
    }

    public Employee createEmployee(EmployeeInput employeeInput) {
        log.debug("Creating new employee: {}", employeeInput.getName());
        
        try {
            Map<String, Object> response = restClient
                    .post()
                    .uri("/employee")
                    .body(employeeInput)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> employeeData = (Map<String, Object>) response.get("data");
                Employee employee = objectMapper.convertValue(employeeData, Employee.class);
                log.debug("Successfully created employee with id: {}", employee.getId());
                return employee;
            }
            
            log.warn("No data found in create employee response");
            return null;
        } catch (Exception e) {
            log.error("Error creating employee: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create employee", e);
        }
    }

    public String deleteEmployeeById(String id) {
        log.debug("Deleting employee with id: {}", id);
        
        try {
            Employee employee = getEmployeeById(id);
            if (employee == null) {
                log.warn("Employee with id {} not found", id);
                return "Employee not found";
            }
            
            Map<String, String> deleteRequest = Map.of("name", employee.getEmployee_name());
            
            Map<String, Object> response = restClient
                    .method(HttpMethod.DELETE)
                    .uri("/employee")
                    .body(deleteRequest)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response != null) {
                Boolean success = (Boolean) response.get("data");
                if (success != null && success) {
                    log.debug("Employee deleted successfully: {}", employee.getEmployee_name());
                    return employee.getEmployee_name();
                } else {
                    log.debug("Delete operation failed for employee: {}", employee.getEmployee_name());
                    return "Delete failed";
                }
            }
            
            log.warn("No response received for delete operation");
            return "Delete failed - no response";
        } catch (Exception e) {
            log.error("Error deleting employee with id {}: {}", id, e.getMessage(), e);
            return "Delete failed - " + e.getMessage();
        }
    }
}
