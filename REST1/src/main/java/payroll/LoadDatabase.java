package payroll;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import payroll.model.Employee;
import payroll.model.Order;
import payroll.repository.EmployeeRepository;
import payroll.repository.OrderRepository;

@Configuration
@Slf4j
class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository) {
        return args -> {
            log.info("Preloading " + employeeRepository.save(new Employee("Bilbo Baggins", "burglar")));
            log.info("Preloading " + employeeRepository.save(new Employee("Frodo Baggins", "thief")));
            log.info("Preloading " + orderRepository.save(new Order("MacBook Pro", Status.IN_PROGRESS)));
            log.info("Preloading " + orderRepository.save(new Order("iPhone", Status.IN_PROGRESS)));
        };
    }
}