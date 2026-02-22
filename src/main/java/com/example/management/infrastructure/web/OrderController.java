package com.example.management.infrastructure.web;

import com.example.management.application.exception.OrderNotFoundException;
import com.example.management.application.port.in.CreateOrderUseCase;
import com.example.management.application.port.in.GetOrderUseCase;
import com.example.management.application.port.in.PayOrderUseCase;
import com.example.management.application.port.in.command.OrderItemInput;
import com.example.management.application.port.out.dto.OrderOutputDTO;
import com.example.management.domain.exception.InvalidOrderStateException;
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
        List<OrderItemInput> items = request.items().stream()
                .map(i -> new OrderItemInput(i.productId(), i.quantity(), i.unitPrice()))
                .toList();
        OrderOutputDTO orderOutput = createOrderUseCase.create(request.customerId(), items);
        CreateOrderResponse response = new CreateOrderResponse(
                orderOutput.orderId(),
                orderOutput.status(),
                orderOutput.totalAmount(),
                orderOutput.createdAt()
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
            OrderOutputDTO orderOutput = payOrderUseCase.pay(orderId);
            PayOrderResponse response = new PayOrderResponse(
                    orderOutput.orderId(),
                    orderOutput.status()
            );
            return ResponseEntity.ok(response);
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidOrderStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        }
    }

    private OrderResponse toOrderResponse(OrderOutputDTO orderOutput) {
        List<OrderItemResponse> items = orderOutput.items().stream()
                .map(item -> new OrderItemResponse(
                        item.productId(),
                        item.quantity(),
                        item.unitPrice()
                ))
                .toList();
        return new OrderResponse(
                orderOutput.orderId(),
                orderOutput.customerId(),
                orderOutput.status(),
                items,
                orderOutput.totalAmount(),
                orderOutput.currency(),
                orderOutput.createdAt()
        );
    }

    public record ErrorResponse(String message) {}
}
