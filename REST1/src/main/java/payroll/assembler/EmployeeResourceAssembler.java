package payroll.assembler;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;
import payroll.controller.EmployeeController;
import payroll.model.Employee;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@Component
public class EmployeeResourceAssembler implements ResourceAssembler<Employee, Resource<Employee>>{

    @Override
    public Resource<Employee> toResource(Employee employee) {
        return new Resource<>(employee,
                linkTo(methodOn(EmployeeController.class).getEmployee(employee.getId())).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).getAllEmployees()).withRel("employees"));
    }
}
