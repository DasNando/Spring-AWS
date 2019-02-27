package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import payroll.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
