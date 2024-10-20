package com.programmingtechie.order_service.service;


import com.programmingtechie.order_service.dto.OrderLineItemsDto;
import com.programmingtechie.order_service.dto.OrderRequest;
import com.programmingtechie.order_service.dto.inventoryResponse;
import com.programmingtechie.order_service.event.OrderPlacedEvent;
import com.programmingtechie.order_service.model.Order;
import com.programmingtechie.order_service.repository.OrderRepository;
import com.programmingtechie.order_service.model.OrderLineItems;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
@RequiredArgsConstructor
@Service
public class OrderService {
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String ,OrderPlacedEvent> kafkaTemplate;
    private final OrderRepository orderRepository;
    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems =orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItems(orderLineItems);
        List<String> skuCodes = order.getOrderLineItems().stream().map(OrderLineItems::getSkuCode).toList();//call inventory service and place order if product is in stock
        inventoryResponse[] inventoryResponseArray =webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",uriBuilder->uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(inventoryResponse[].class)
                .block();
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(inventoryResponse::isInStock);
        if (allProductsInStock) {
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopic",new OrderPlacedEvent(order.getOrderNumber()));
            return "Order Placed";

        }else {
            throw new IllegalStateException("product not in stock please try again");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}