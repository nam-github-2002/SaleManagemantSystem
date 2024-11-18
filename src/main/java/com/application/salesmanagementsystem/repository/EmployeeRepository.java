package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Category;
import com.application.salesmanagementsystem.model.Employee;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Blob;
import java.time.LocalDate;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    @Query("SELECT e FROM Employee e WHERE " +
            "STR(e.employeeId) = :keyword OR " +
            "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.role) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.status) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.department) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Employee> findAllByKeyword(String keyword, Pageable pageable);


//    @Modifying
//    @Transactional
//    @Query("UPDATE Employee e SET e.name = :name, e.phone = :phone, e.email = :email," +
//            " e.department = :department, e.gender = :gender, e.dateOfBirth = :dateOfBirth," +
//            " e.hireDate = :hireDate, e.salary = :salary, e.status = :status, e.image = :image " +
//            "WHERE e.employeeId = :id")
//    void updateEmployeeDetails(Integer id, String name, String phone, String email, String department, String gender, LocalDate dateOfBirth, LocalDate hireDate, BigDecimal salary, String status, Blob image);

    Employee findTopByOrderByEmployeeIdDesc();
    Employee findByUsername(String username);
}