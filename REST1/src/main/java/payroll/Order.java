package payroll;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name="CUSTOMER_ORDER")
public class Order {

    private String description;
    private Status status;

    private @Id @GeneratedValue Long id;

    Order(String description, Status status){
        this.description = description;
        this.status = status;
    }
}
