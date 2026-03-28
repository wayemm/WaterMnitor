package com.example.watermonitor.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StationVO {
    private Long id;
    private String name;
    private String location;
    private BigDecimal warnLevel;
    private BigDecimal dangerLevel;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
