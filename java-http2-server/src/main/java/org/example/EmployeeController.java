package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        return optionalEmployee.orElse(null);
    }

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeRepository.save(employee);
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee updatedEmployee) {
        updatedEmployee.setId(id); // Ensure ID is set correctly
        return employeeRepository.save(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);
    }
    @GetMapping("/sortedByName")
    public List<String> getEmployeeNamesSortedByName(HttpServletRequest httpServletRequest,
                                                     HttpServletResponse httpServletResponse) {
        return employeeService.getEmployeeNamesSortedBy("name");
    }

    // 2. Get employee names sorted by ID
    @GetMapping("/sortedById")
    public List<String> getEmployeeNamesSortedById() throws InterruptedException {
        Thread.sleep(10000);
        return employeeService.getEmployeeNamesSortedBy("id");
    }

    // 3. Get employee names sorted by salary
    @GetMapping("/sortedBySalary")
    public List<String> getEmployeeNamesSortedBySalary() {
        return employeeService.getEmployeeNamesSortedBy("salary");
    }

    // 4. Get employee names sorted by designation
    @GetMapping("/sortedByDesignation")
    public List<String> getEmployeeNamesSortedByDesignation() {
        return employeeService.getEmployeeNamesSortedBy("designation");
    }

    // 5. Get employees ordered by name
    @GetMapping("/orderByName")
    public List<Employee> getEmployeesOrderedByName() {
        return employeeService.getEmployeesOrderedByName();
    }

    // 6. Get employees ordered by salary
    @GetMapping("/orderBySalary")
    public List<Employee> getEmployeesOrderedBySalary() {
        return employeeService.getEmployeesOrderedBySalary();
    }
}
