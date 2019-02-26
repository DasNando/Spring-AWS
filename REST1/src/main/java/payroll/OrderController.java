package payroll;

import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class OrderController {

    private OrderRepository orderRepository;
    private OrderResourceAssembler orderResourceAssembler;

    OrderController(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    @GetMapping("/orders")
    List<Resource<Order>> getAllOrders(){
        return orderRepository.findAll().stream()
                .map(orderResourceAssembler::toResource)
                .collect(Collectors.toList());
    }

    @GetMapping("/orders/{id}")
    Resource<Order> getOrder(@PathVariable("id") Long id){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return orderResourceAssembler.toResource(order);
    }

    @PostMapping("/orders")
    ResponseEntity<?> newOrder(@RequestBody Order newOrder) throws URISyntaxException {
        Resource<Order> resource = orderResourceAssembler.toResource(orderRepository.save(newOrder));
        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

    @PutMapping("/orders/{id}")
    ResponseEntity<?> updateOrder(@PathVariable("id") Long id, @RequestBody Order newOrder) throws URISyntaxException {
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
    ResponseEntity<?> removeOrder(@PathVariable("id") Long id){
        orderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
