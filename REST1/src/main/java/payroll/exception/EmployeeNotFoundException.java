package payroll.exception;

public class EmployeeNotFoundException extends RuntimeException{

    public EmployeeNotFoundException(long id){
        super("Could not find Employee with id: " + id);
    }
}
