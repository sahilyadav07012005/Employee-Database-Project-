package com.example.studentmanagement.service;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import com.example.studentmanagement.entity.Employee;

@Service
public class EmployeeJdbcService {
    private final JdbcTemplate jdbcTemplate;

    public EmployeeJdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Employee> rowMapper = (rs, rowNum) -> {
        Employee e = new Employee();
        e.setId(rs.getLong("id"));
        e.setName(rs.getString("name"));
        e.setAddress(rs.getString("address"));
        e.setEmail(rs.getString("email"));
        return e;
    };

    public Long addEmployee(String name, String address, String email) {
        String sql = "INSERT INTO employee (name, address, email) VALUES (?, ?, ?) RETURNING id";
        return jdbcTemplate.queryForObject(sql, Long.class, name, address, email);
    }

    public List<Employee> getAllEmployees() {
        return jdbcTemplate.query("SELECT * FROM employee ORDER BY id", rowMapper);
    }

    public List<Employee> searchEmployee(Long id) {
        return jdbcTemplate.query("SELECT * FROM employee WHERE id = ?", rowMapper, id);
    }

    public int updateEmployee(Long id, String name, String address, String email) {
        String sql = "UPDATE employee SET name = ?, address = ?, email = ? WHERE id = ?";
        return jdbcTemplate.update(sql, name, address, email, id);
    }

    public int deleteEmployee(Long id) {
        return jdbcTemplate.update("DELETE FROM employee WHERE id = ?", id);
    }
}
