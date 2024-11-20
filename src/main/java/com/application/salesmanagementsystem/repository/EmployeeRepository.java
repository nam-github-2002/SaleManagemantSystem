package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

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

    // Thống kê số lượng nhân viên theo phòng ban
    @Query("SELECT e.department, COUNT(e) FROM Employee e GROUP BY e.department")
    List<Object[]> countEmployeesByDepartment();

    Employee findTopByOrderByEmployeeIdDesc();
    Employee findByUsername(String username);
}