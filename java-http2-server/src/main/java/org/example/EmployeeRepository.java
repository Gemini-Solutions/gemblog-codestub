package org.example;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Add custom query methods if needed
    List<Employee> findAllByOrderByNameAsc();
    List<Employee> findAllByOrderByIdAsc();
    List<Employee> findAllByOrderBySalaryAsc();
    List<Employee> findAllByOrderByDesignationAsc();
}
