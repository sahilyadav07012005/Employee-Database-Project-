package com.example.studentmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.studentmanagement.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
