package com.example.watermonitor.model.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WaterRecord {
    private Long id;
    private Long stationId;
    private BigDecimal waterLevel;
    private LocalDateTime recordedAt;
    private LocalDateTime createdAt;
}
