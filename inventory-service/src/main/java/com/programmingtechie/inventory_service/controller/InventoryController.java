package com.programmingtechie.inventory_service.controller;
 // Ensure this is the correct import
import com.programmingtechie.inventory_service.dto.inventoryResponse;
import com.programmingtechie.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    // Change here to accept skuCode as a request parameter instead of path variable
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<inventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        return inventoryService.isInStock(skuCode);
    }
}
