package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.*;

import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private EmployeeController employeeController;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
        // Verify that the Spring context loads successfully
        assertNotNull(applicationContext, "Application context should be loaded");
    }

    @Test
    void controllerIsLoaded() {
        // Verify that the main controller bean is created and injected
        assertNotNull(employeeController, "EmployeeController should be loaded");
    }

    @Test
    void serviceIsLoaded() {
        // Verify that the service layer bean is created and injected
        assertNotNull(employeeService, "EmployeeService should be loaded");
    }

    @Test
    void applicationStartsOnRandomPort() {
        // Verify that the application starts on the assigned random port
        assertTrue(port > 0, "Application should start on a valid port");
    }

}
