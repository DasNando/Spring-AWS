package payroll.controller;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.assembler.OrderResourceAssembler;
import payroll.exception.OrderNotFoundException;
import payroll.model.Order;
import payroll.model.enums.Status;
import payroll.repository.OrderRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class OrderController {

    private OrderRepository orderRepository;
    private OrderResourceAssembler orderResourceAssembler;

    public OrderController(OrderRepository orderRepository, OrderResourceAssembler orderResourceAssembler){
        this.orderRepository = orderRepository;
        this.orderResourceAssembler = orderResourceAssembler;
    }

    @GetMapping("/orders")
    public Resources<Resource<Order>> getAllOrders(){
        List<Resource<Order>> orders = orderRepository.findAll().stream()
                .map(orderResourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(orders, linkTo(methodOn(OrderController.class).getAllOrders()).withSelfRel());
    }

    @GetMapping("/orders/{id}")
    public Resource<Order> getOrder(@PathVariable("id") Long id){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return orderResourceAssembler.toResource(order);
    }

    @PostMapping("/orders")
    public ResponseEntity<?> newOrder(@RequestBody Order newOrder) throws URISyntaxException {
        newOrder.setStatus(Status.IN_PROGRESS);
        Resource<Order> resource = orderResourceAssembler.toResource(orderRepository.save(newOrder));
        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable("id") Long id, @RequestBody Order newOrder) throws URISyntaxException {
        Order updatedOrder = orderRepository.findById(id).map(order -> {
            order.setDescription(newOrder.getDescription());
            order.setStatus(newOrder.getStatus());
            return orderRepository.save(order);
        }).orElseGet(() -> {
            newOrder.setId(id);
            return orderRepository.save(newOrder);
        });
        Resource<Order> resource = orderResourceAssembler.toResource(updatedOrder);
        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource.getContent());
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<?> removeOrder(@PathVariable("id") Long id){
        orderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable("id") Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        if(order.getStatus() == Status.IN_PROGRESS){
            order.setStatus(Status.CANCELLED);
            return ResponseEntity.ok(orderResourceAssembler.toResource(orderRepository.save(order)));
        }
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new VndErrors.VndError("Method not allowed", "You can't cancel an order that is in the " + order.getStatus() + " status"));
    }

    @PutMapping("orders/{id}/complete")
    public ResponseEntity<?> completeOrder(@PathVariable("id") Long id){
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        if(order.getStatus() == Status.IN_PROGRESS){
            order.setStatus(Status.COMPLETED);
            return ResponseEntity.ok(orderResourceAssembler.toResource(orderRepository.save(order)));
        }

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new VndErrors.VndError("Method not allowed", "You can't cancel an order that is in the " + order.getStatus() + " status"));
    }
}




















