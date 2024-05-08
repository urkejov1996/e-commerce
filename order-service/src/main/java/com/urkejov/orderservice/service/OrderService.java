package com.urkejov.orderservice.service;

import com.urkejov.orderservice.dto.InventoryResponse;
import com.urkejov.orderservice.dto.OrderItemsDto;
import com.urkejov.orderservice.dto.OrderRequest;
import com.urkejov.orderservice.model.Order;
import com.urkejov.orderservice.model.OrderItems;
import com.urkejov.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest) {

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderItems> orderItems = orderRequest.getOrderItemsDtoList()z
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderItemsList(orderItems);
        List<String> skuCodes = order.getOrderItemsList().stream()
                .map(OrderItems::getSkuCode)
                .toList();
        InventoryResponse[] inventoryResponseArray = webClient.get()
                .uri("http://localhost:8082/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

        if (Boolean.TRUE.equals(allProductsInStock)) {

            orderRepository.save(order);

        } else throw new IllegalArgumentException("Product is not in stock, please try again later");
    }

    private OrderItems mapToDto(OrderItemsDto orderItemsDto) {
        return OrderItems.builder()
                .skuCode(orderItemsDto.getSkuCode())
                .price(orderItemsDto.getPrice())
                .quantity(orderItemsDto.getQuantity())
                .build();
    }
}
