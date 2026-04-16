package com.kfc.kfc_backend.controller;

import com.kfc.kfc_backend.entity.SalesDaily;
import com.kfc.kfc_backend.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "http://localhost:5173")
public class SalesController {

    @Autowired
    private SalesService salesService;

    // GET /api/sales?start=yyyy-MM-dd&end=yyyy-MM-dd
    @GetMapping
    public List<SalesDaily> list(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        return salesService.findByDateRange(start, end);
    }

    // POST /api/sales
    @PostMapping
    public SalesDaily add(@RequestBody SalesDaily sales) {
        return salesService.save(sales);
    }

    // PUT /api/sales/{id}
    @PutMapping("/{id}")
    public SalesDaily update(@PathVariable Long id, @RequestBody SalesDaily sales) {
        sales.setId(id);
        return salesService.save(sales);
    }

    // DELETE /api/sales/{id}
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        salesService.deleteById(id);
    }
}
