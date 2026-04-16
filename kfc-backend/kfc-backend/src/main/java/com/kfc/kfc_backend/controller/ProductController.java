package com.kfc.kfc_backend.controller;

import com.kfc.kfc_backend.entity.Product;
import com.kfc.kfc_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")   // 改为复数
@CrossOrigin(origins = "http://localhost:5173") // 允许前端跨域
public class ProductController {

    @Autowired
    private ProductService productService;

    // GET /api/products   -> 获取所有商品
    @GetMapping
    public List<Product> list() {
        return productService.findAll();
    }

    // POST /api/products  -> 新增商品
    @PostMapping
    public Product add(@RequestBody Product product) {
        return productService.save(product);
    }

    // PUT /api/products/{id} -> 更新商品
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return productService.save(product);
    }

    // DELETE /api/products/{id} -> 删除商品
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.deleteById(id);
    }
}
