package com.example.watermonitor.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AlertRecordVO {
    private Long id;
    private Long stationId;
    private String stationName;
    private Long recordId;
    private Integer alertType;
    private String alertTypeDesc;
    private BigDecimal waterLevel;
    private Integer status;
    private String statusDesc;
    private LocalDateTime handledAt;
    private LocalDateTime createdAt;
}
