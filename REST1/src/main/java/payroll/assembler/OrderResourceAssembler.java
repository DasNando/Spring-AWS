package payroll.assembler;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;
import payroll.Status;
import payroll.controller.OrderController;
import payroll.model.Order;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class OrderResourceAssembler implements ResourceAssembler<Order, Resource<Order>> {

    @Override
    public Resource<Order> toResource(Order order) {
        Resource<Order> resource = new Resource<>(order,
                linkTo(methodOn(OrderController.class).getOrder(order.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"));

        if(order.getStatus() == Status.IN_PROGRESS){
            resource.add(linkTo(methodOn(OrderController.class).cancelOrder(order.getId())).withRel("cancel"));
            resource.add(linkTo(methodOn(OrderController.class).completeOrder(order.getId())).withRel("complete"));
        }
        return resource;

    }
}
