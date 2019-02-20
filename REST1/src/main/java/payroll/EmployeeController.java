package payroll;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@RestController
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    EmployeeController(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/employees")
    Resources<Resource<Employee>> getAllEmployees(){
        List<Resource<Employee>> employees = employeeRepository.findAll().stream()
                .map(employee -> new Resource<>(employee,
                        linkTo(methodOn(EmployeeController.class).getEmployee(employee.getId())).withSelfRel(),
                        linkTo(methodOn(EmployeeController.class).getAllEmployees()).withRel("employees")))
                .collect(Collectors.toList());

        return new Resources<>(employees,
                linkTo(methodOn(EmployeeController.class).getAllEmployees()).withSelfRel());
    }

    @GetMapping("/employees/{id}")
    Resource<Employee> getEmployee(@PathVariable("id") long id){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return new Resource<>(employee,
                linkTo(methodOn(EmployeeController.class).getEmployee(id)).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).getAllEmployees()).withRel("employees"));
    }

    @PostMapping("/employees")
    Employee newEmployee(@RequestBody Employee newEmployee){
        return employeeRepository.save(newEmployee);
    }

    @PutMapping("/employees/{id}")
    void updateEmployee(@RequestBody Employee newEmployee, @PathVariable("id") long id){
        employeeRepository.findById(id).map(employee -> {
            employee.setName(newEmployee.getName());
            employee.setRole(newEmployee.getRole());
            return employeeRepository.save(employee);
        })
        .orElseGet(() -> {
            newEmployee.setId(id);
            return employeeRepository.save(newEmployee);
        });
    }

    @DeleteMapping("/employees/{id}")
    void removeEmployee(@PathVariable("id") long id){
        employeeRepository.deleteById(id);
    }

}
