package com.example.watermonitor.model.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AlertRecord {
    private Long id;
    private Long stationId;
    private Long recordId;
    private Integer alertType;
    private BigDecimal waterLevel;
    private Integer status;
    private LocalDateTime handledAt;
    private LocalDateTime createdAt;
}
