package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<Employee> getAllEmployees();

    Optional<Employee> getEmployeeById(Integer id);

    Employee saveEmployee(Employee employee);

    void deleteEmployee(Integer id);

    Employee findByUsername(String username);

    boolean checkPassword(Employee employee, String password);
}
