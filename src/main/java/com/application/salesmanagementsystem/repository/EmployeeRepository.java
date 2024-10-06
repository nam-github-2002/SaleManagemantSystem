package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Category;
import com.application.salesmanagementsystem.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Employee findByUsername(String username);
}