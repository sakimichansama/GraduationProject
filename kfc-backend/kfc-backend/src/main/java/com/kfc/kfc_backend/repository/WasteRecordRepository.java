package com.kfc.kfc_backend.repository;

import com.kfc.kfc_backend.entity.WasteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface WasteRecordRepository extends JpaRepository<WasteRecord, Long> {
    @Query("SELECT SUM(w.quantity * p.purchasePrice) FROM WasteRecord w JOIN Product p ON w.productId = p.id WHERE w.createTime BETWEEN :start AND :end")
    Double sumWasteCostBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
