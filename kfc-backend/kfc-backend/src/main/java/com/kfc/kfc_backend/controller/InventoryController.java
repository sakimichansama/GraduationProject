package com.kfc.kfc_backend.controller;

import com.kfc.kfc_backend.entity.InStockRecord;
import com.kfc.kfc_backend.entity.OutStockRecord;
import com.kfc.kfc_backend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:5173")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // POST /api/inventory/in
    @PostMapping("/in")
    public void inStock(@RequestParam Long productId,
                        @RequestParam Integer quantity,
                        @RequestParam Double price,
                        @RequestParam String supplier) {
        inventoryService.inStock(productId, quantity, price, supplier);
    }

    // POST /api/inventory/out
    @PostMapping("/out")
    public void outStock(@RequestParam Long productId,
                         @RequestParam Integer quantity,
                         @RequestParam String reason) {
        inventoryService.outStock(productId, quantity, reason);
    }

    // GET /api/inventory/in-records
    @GetMapping("/in-records")
    public List<InStockRecord> inRecords() {
        return inventoryService.getInRecords();
    }

    // GET /api/inventory/out-records
    @GetMapping("/out-records")
    public List<OutStockRecord> outRecords() {
        return inventoryService.getOutRecords();
    }
}