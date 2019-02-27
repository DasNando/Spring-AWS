package payroll.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id){
        super("Could not find order with id: " + id);
    }
}
