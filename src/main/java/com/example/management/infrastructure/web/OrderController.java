package com.example.management.infrastructure.web;

import com.example.management.application.service.CreateOrderService;
import com.example.management.application.service.GetOrderService;
import com.example.management.application.service.PayOrderService;
import com.example.management.domain.exception.InvalidOrderStateException;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.infrastructure.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for Order Management API.
 * Base path: /api/v1
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderService createOrderService;
    private final GetOrderService getOrderService;
    private final PayOrderService payOrderService;

    public OrderController(CreateOrderService createOrderService,
                           GetOrderService getOrderService,
                           PayOrderService payOrderService) {
        this.createOrderService = createOrderService;
        this.getOrderService = getOrderService;
        this.payOrderService = payOrderService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        List<CreateOrderService.OrderItemInput> items = request.items().stream()
                .map(i -> new CreateOrderService.OrderItemInput(i.productId(), i.quantity(), i.unitPrice()))
                .toList();
        Order order = createOrderService.create(request.customerId(), items);
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
        return getOrderService.getById(orderId)
                .map(this::toOrderResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<?> payOrder(@PathVariable UUID orderId) {
        try {
            Order order = payOrderService.pay(orderId);
            PayOrderResponse response = new PayOrderResponse(
                    order.getId().getValue(),
                    order.getStatus().name()
            );
            return ResponseEntity.ok(response);
        } catch (PayOrderService.OrderNotFoundException e) {
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
