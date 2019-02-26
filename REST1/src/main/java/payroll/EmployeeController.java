package payroll;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@RestController
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    private final EmployeeResourceAssembler employeeResourceAssembler;

    EmployeeController(EmployeeRepository employeeRepository, EmployeeResourceAssembler employeeResourceAssembler) {
        this.employeeRepository = employeeRepository;
        this.employeeResourceAssembler = employeeResourceAssembler;
    }

    @GetMapping("/employees")
    Resources<Resource<Employee>> getAllEmployees() {
        List<Resource<Employee>> employees = employeeRepository.findAll().stream()
                .map(employeeResourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(employees,
                linkTo(methodOn(EmployeeController.class).getAllEmployees()).withSelfRel());
    }

    @GetMapping("/employees/{id}")
    Resource<Employee> getEmployee(@PathVariable("id") Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return employeeResourceAssembler.toResource(employee);
    }

    @PostMapping("/employees")
    ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) throws URISyntaxException {
        Resource<Employee> resource = employeeResourceAssembler.toResource(employeeRepository.save(newEmployee));
        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

    @PutMapping("/employees/{id}")
    ResponseEntity<?> updateEmployee(@RequestBody Employee newEmployee, @PathVariable("id") Long id) throws URISyntaxException {
        Employee updatedEmployee = employeeRepository.findById(id).map(employee -> {
            employee.setName(newEmployee.getName());
            employee.setRole(newEmployee.getRole());
            return employeeRepository.save(employee);
        })
                .orElseGet(() -> {
                    newEmployee.setId(id);
                    return employeeRepository.save(newEmployee);
                });

        Resource<Employee> resource = employeeResourceAssembler.toResource(updatedEmployee);

        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

    @DeleteMapping("/employees/{id}")
    ResponseEntity<?> removeEmployee(@PathVariable("id") Long id) {
        employeeRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
