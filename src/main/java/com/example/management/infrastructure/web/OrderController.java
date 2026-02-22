package com.example.management.infrastructure.web;

import com.example.management.application.exception.OrderNotFoundException;
import com.example.management.application.port.in.CreateOrderUseCase;
import com.example.management.application.port.in.GetOrderUseCase;
import com.example.management.application.port.in.PayOrderUseCase;
import com.example.management.domain.exception.InvalidOrderStateException;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.infrastructure.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for Order Management API.
 * Base path: /api/v1
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final PayOrderUseCase payOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                           GetOrderUseCase getOrderUseCase,
                           PayOrderUseCase payOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.payOrderUseCase = payOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        List<CreateOrderUseCase.OrderItemInput> items = request.items().stream()
                .map(i -> new CreateOrderUseCase.OrderItemInput(i.productId(), i.quantity(), i.unitPrice()))
                .toList();
        Order order = createOrderUseCase.create(request.customerId(), items);
        CreateOrderResponse response = new CreateOrderResponse(
                order.getId().getValue(),
                order.getStatus().name(),
                order.getTotalAmount().getAmount(),
                order.getCreatedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        return getOrderUseCase.getById(orderId)
                .map(this::toOrderResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<?> payOrder(@PathVariable UUID orderId) {
        try {
            Order order = payOrderUseCase.pay(orderId);
            PayOrderResponse response = new PayOrderResponse(
                    order.getId().getValue(),
                    order.getStatus().name()
            );
            return ResponseEntity.ok(response);
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidOrderStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        }
    }

    private OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice().getAmount()
                ))
                .toList();
        return new OrderResponse(
                order.getId().getValue(),
                order.getCustomerId(),
                order.getStatus().name(),
                items,
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency(),
                order.getCreatedAt()
        );
    }

    public record ErrorResponse(String message) {}
}
