package com.programmingtechie.inventory_service.service;

import com.programmingtechie.inventory_service.dto.inventoryResponse;
import com.programmingtechie.inventory_service.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Transactional
@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional()
    public List<inventoryResponse> isInStock(List<String> skuCode) {  // Use skuCode as the parameter
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory ->
                    inventoryResponse.builder().skuCode(inventory.getSkuCode())
                            .isInStock(inventory.getQuantity() > 0)
                            .build()
                )
                .toList();
    }
}
