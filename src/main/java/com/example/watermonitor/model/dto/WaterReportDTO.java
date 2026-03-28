package com.example.watermonitor.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WaterReportDTO {
    @NotNull(message = "站点ID不能为空")
    private Long stationId;

    @NotNull(message = "水位值不能为空")
    @Positive(message = "水位值必须为正数")
    private BigDecimal waterLevel;

    private LocalDateTime recordedAt;
}
