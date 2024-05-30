package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    private final Random random = new Random();


    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        return optionalEmployee.orElse(null);
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        updatedEmployee.setId(id); // Ensure ID is set correctly
        return employeeRepository.save(updatedEmployee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public List<String> getEmployeeNamesSortedBy(String criteria) {
        List<Employee> employees;
        switch (criteria) {
            case "name":
                employees = employeeRepository.findAllByOrderByNameAsc();
                break;
            case "id":
                employees = employeeRepository.findAllByOrderByIdAsc();
                break;
            case "salary":
                employees = employeeRepository.findAllByOrderBySalaryAsc();
                break;
            case "designation":
                employees = employeeRepository.findAllByOrderByDesignationAsc();
                break;
            default:
                throw new IllegalArgumentException("Invalid criteria: " + criteria);
        }
        addRandomDelay();
        return employees.stream().map(Employee::getName).collect(Collectors.toList());
    }

    public List<Employee> getEmployeesOrderedByName() {
        return employeeRepository.findAllByOrderByNameAsc();
    }

    public List<Employee> getEmployeesOrderedBySalary() {
        return employeeRepository.findAllByOrderBySalaryAsc();
    }

    public void addRandomDelay() {
        int delay = random.nextInt(200) + 100; // Random delay between 100ms and 300ms
        try {



            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
