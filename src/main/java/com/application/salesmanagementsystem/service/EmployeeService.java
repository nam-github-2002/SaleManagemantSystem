package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Page<Employee> getAllEmployees(Pageable pageable);

    Employee getEmployeeById(Integer id);

    Employee saveEmployee(Employee employee);

    void deleteEmployee(Integer id);

    Page<Employee> searchAllField(String keyword, Pageable pageable);

    boolean checkPassword(Employee employee, String password);

    int generateEmployeeId();

    Employee findByUsername(String username);
}
