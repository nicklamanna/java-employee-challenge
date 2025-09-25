package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.debug("Fetching all employees");
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        log.debug("Searching employees by name: {}", searchString);
        return ResponseEntity.ok(employeeService.getEmployeesByNameSearch(searchString));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        log.debug("Fetching employee by id: {}", id);
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.debug("Fetching highest salary");
        return ResponseEntity.ok(employeeService.getHighestSalaryOfEmployees());
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.debug("Fetching top 10 highest earning employees");
        return ResponseEntity.ok(employeeService.getTopTenHighestEarningEmployeeNames());
    }

    @Override
    @PostMapping()
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeInput employeeInput) {
        log.debug("Creating new employee: {}", employeeInput.getName());
        return ResponseEntity.ok(employeeService.createEmployee(employeeInput));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        log.debug("Deleting employee with id: {}", id);
        return ResponseEntity.ok(employeeService.deleteEmployeeById(id));
    }
}
