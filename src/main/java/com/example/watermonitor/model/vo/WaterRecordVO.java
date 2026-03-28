package com.example.watermonitor.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WaterRecordVO {
    private Long id;
    private Long stationId;
    private String stationName;
    private BigDecimal waterLevel;
    private LocalDateTime recordedAt;
    private LocalDateTime createdAt;
}
