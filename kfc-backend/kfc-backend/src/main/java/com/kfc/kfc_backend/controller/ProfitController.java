package com.kfc.kfc_backend.controller;

import com.kfc.kfc_backend.service.ProfitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/profit")
@CrossOrigin(origins = "http://localhost:5173")
public class ProfitController {

    @Autowired
    private ProfitService profitService;

    // GET /api/profit/month?month=yyyy-MM
    @GetMapping("/month")
    public Map<String, Object> getMonthlyProfit(@RequestParam String month) {
        String[] parts = month.split("-");
        int year = Integer.parseInt(parts[0]);
        int monthNum = Integer.parseInt(parts[1]);
        return profitService.getMonthlyProfitReport(year, monthNum);
    }

    // GET /api/profit/export?month=yyyy-MM
    @GetMapping("/export")
    public void exportExcel(@RequestParam String month) {
        // 导出功能待实现，暂时抛出异常
        throw new UnsupportedOperationException("导出功能待实现");
    }
}
