package com.example.studentmanagement.runner;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.studentmanagement.entity.Employee;
import com.example.studentmanagement.repository.EmployeeRepository;
import com.example.studentmanagement.service.EmployeeJdbcService;

@Component
public class ConsoleMenuRunner implements CommandLineRunner {
    private final EmployeeJdbcService jdbcService;
    private final EmployeeRepository repository;

    public ConsoleMenuRunner(EmployeeJdbcService jdbcService, EmployeeRepository repository) {
        this.jdbcService = jdbcService;
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        try (Scanner sc = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.println("\n===== EMPLOYEE MANAGEMENT SYSTEM =====");
                System.out.println("1. Add Employee - JDBC");
                System.out.println("2. View Employees - JDBC");
                System.out.println("3. Search Employee - JDBC");
                System.out.println("4. Update Employee - JDBC");
                System.out.println("5. Delete Employee - JDBC");
                System.out.println("-------------------------------------");
                System.out.println("6. Add Employee - Hibernate/JPA");
                System.out.println("7. View Employees - Hibernate/JPA");
                System.out.println("8. Search Employee - Hibernate/JPA");
                System.out.println("9. Update Employee - Hibernate/JPA");
                System.out.println("10. Delete Employee - Hibernate/JPA");
                System.out.println("11. Exit");
                System.out.print("Enter choice: ");
                int choice;
                try { choice = Integer.parseInt(sc.nextLine().trim()); }
                catch (NumberFormatException ex) { System.out.println("Enter a valid number."); continue; }
                try {
                    switch (choice) {
                        case 1 -> addJdbc(sc);
                        case 2 -> printEmployees(jdbcService.getAllEmployees());
                        case 3 -> searchJdbc(sc);
                        case 4 -> updateJdbc(sc);
                        case 5 -> deleteJdbc(sc);
                        case 6 -> addJpa(sc);
                        case 7 -> printEmployees(repository.findAll());
                        case 8 -> searchJpa(sc);
                        case 9 -> updateJpa(sc);
                        case 10 -> deleteJpa(sc);
                        case 11 -> { running = false; System.out.println("Application menu closed."); }
                        default -> System.out.println("Invalid choice.");
                    }
                } catch (Exception ex) { System.out.println("Operation failed: " + ex.getMessage()); }
            }
        }
    }

    private Employee readEmployee(Scanner sc) {
        System.out.print("Enter name: "); String name = sc.nextLine().trim();
        System.out.print("Enter address: "); String address = sc.nextLine().trim();
        System.out.print("Enter email: "); String email = sc.nextLine().trim();
        if (name.isBlank() || address.isBlank() || email.isBlank()) {
            System.out.println("All fields are required."); return null;
        }
        return new Employee(name, address, email);
    }

    private Long readId(Scanner sc) {
        System.out.print("Enter employee ID: ");
        try { long id = Long.parseLong(sc.nextLine().trim()); if (id > 0) return id; }
        catch (NumberFormatException ignored) {}
        System.out.println("Please enter a valid positive ID."); return null;
    }

    private void printEmployees(List<Employee> employees) {
        if (employees.isEmpty()) { System.out.println("No employees found."); return; }
        employees.forEach(System.out::println);
    }

    private void addJdbc(Scanner sc) {
        Employee e = readEmployee(sc); if (e == null) return;
        System.out.println("Employee added using JDBC. ID: " + jdbcService.addEmployee(e.getName(), e.getAddress(), e.getEmail()));
    }
    private void searchJdbc(Scanner sc) {
        Long id = readId(sc); if (id != null) printEmployees(jdbcService.searchEmployee(id));
    }
    private void updateJdbc(Scanner sc) {
        Long id = readId(sc); if (id == null) return;
        Employee e = readEmployee(sc); if (e == null) return;
        int rows = jdbcService.updateEmployee(id, e.getName(), e.getAddress(), e.getEmail());
        System.out.println(rows > 0 ? "Employee updated using JDBC." : "Employee not found.");
    }
    private void deleteJdbc(Scanner sc) {
        Long id = readId(sc); if (id == null) return;
        System.out.println(jdbcService.deleteEmployee(id) > 0 ? "Employee deleted using JDBC." : "Employee not found.");
    }

    private void addJpa(Scanner sc) {
        Employee e = readEmployee(sc); if (e == null) return;
        System.out.println("Employee added using Hibernate/JPA: " + repository.save(e));
    }
    private void searchJpa(Scanner sc) {
        Long id = readId(sc); if (id == null) return;
        Optional<Employee> e = repository.findById(id);
        if (e.isPresent()) System.out.println(e.get()); else System.out.println("Employee not found.");
    }
    private void updateJpa(Scanner sc) {
        Long id = readId(sc); if (id == null) return;
        Optional<Employee> result = repository.findById(id);
        if (result.isEmpty()) { System.out.println("Employee not found."); return; }
        Employee input = readEmployee(sc); if (input == null) return;
        Employee e = result.get();
        e.setName(input.getName()); e.setAddress(input.getAddress()); e.setEmail(input.getEmail());
        System.out.println("Employee updated using Hibernate/JPA: " + repository.save(e));
    }
    private void deleteJpa(Scanner sc) {
        Long id = readId(sc); if (id == null) return;
        if (!repository.existsById(id)) { System.out.println("Employee not found."); return; }
        repository.deleteById(id); System.out.println("Employee deleted using Hibernate/JPA.");
    }
}
