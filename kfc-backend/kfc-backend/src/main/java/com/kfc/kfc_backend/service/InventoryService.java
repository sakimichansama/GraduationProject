package com.kfc.kfc_backend.service;

import com.kfc.kfc_backend.entity.InStockRecord;
import com.kfc.kfc_backend.entity.OutStockRecord;
import com.kfc.kfc_backend.entity.Product;
import com.kfc.kfc_backend.repository.InStockRecordRepository;
import com.kfc.kfc_backend.repository.OutStockRecordRepository;
import com.kfc.kfc_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InStockRecordRepository inStockRecordRepository;
    @Autowired
    private OutStockRecordRepository outStockRecordRepository;

    @Transactional
    public void inStock(Long productId, Integer quantity, Double price, String supplier) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("The product does not exist."));
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);

        InStockRecord record = new InStockRecord();
        record.setProductId(productId);
        record.setQuantity(quantity);
        record.setPrice(price);
        record.setSupplier(supplier);
        record.setCreateTime(LocalDateTime.now());
        inStockRecordRepository.save(record);
    }

    @Transactional
    public void outStock(Long productId, Integer quantity, String reason) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("The product does not exist."));
        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient inventory, current inventory:" + product.getStock());
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        OutStockRecord record = new OutStockRecord();
        record.setProductId(productId);
        record.setQuantity(quantity);
        record.setReason(reason);
        record.setCostPrice(product.getPurchasePrice());
        record.setCreateTime(LocalDateTime.now());
        outStockRecordRepository.save(record);
    }

    public List<InStockRecord> getInRecords() {
        return inStockRecordRepository.findAllByOrderByCreateTimeDesc();
    }

    public List<OutStockRecord> getOutRecords() {
        return outStockRecordRepository.findAllByOrderByCreateTimeDesc();
    }
}
