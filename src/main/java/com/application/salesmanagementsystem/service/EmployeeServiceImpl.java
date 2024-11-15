package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Page<Employee> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    @Override
    public Employee getEmployeeById(Integer id) {
        return employeeRepository.findById(id).get();
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployee(Integer id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public Page<Employee> searchAllField(String keyword, Pageable pageable) {
        return employeeRepository.findAllByKeyword(keyword, pageable);
    }

    @Override
    public boolean checkPassword(Employee employee, String password) {
        return employee.getPassword().equals(password);
    }

    @Override
    public int generateEmployeeId() {
        Employee employee = employeeRepository.findTopByOrderByEmployeeIdDesc();
        return employee.getEmployeeId() + 1;
    }

    @Override
    public Employee findByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }
}