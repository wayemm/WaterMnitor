package com.example.watermonitor.controller;

import com.example.watermonitor.common.Result;
import com.example.watermonitor.model.dto.WaterReportDTO;
import com.example.watermonitor.model.pojo.Station;
import com.example.watermonitor.model.pojo.WaterRecord;
import com.example.watermonitor.model.vo.WaterRecordVO;
import com.example.watermonitor.service.StationService;
import com.example.watermonitor.service.WaterRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/water")
public class WaterRecordController {
    
    @Autowired
    private WaterRecordService waterRecordService;
    
    @Autowired
    private StationService stationService;
    
    @PostMapping("/report")
    public Result<Void> reportWaterLevel(@Valid @RequestBody WaterReportDTO dto) {
        waterRecordService.reportWaterLevel(dto);
        return Result.success();
    }
    
    @GetMapping("/records")
    public Result<List<WaterRecordVO>> getRecords(
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        List<WaterRecord> records = waterRecordService.getRecords(stationId, startTime, endTime);
        List<WaterRecordVO> voList = records.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }
    
    @GetMapping("/latest/{stationId}")
    public Result<WaterRecordVO> getLatestRecord(@PathVariable Long stationId) {
        WaterRecord record = waterRecordService.getLatestRecord(stationId);
        if (record == null) {
            return Result.success(null);
        }
        return Result.success(convertToVO(record));
    }
    
    private WaterRecordVO convertToVO(WaterRecord record) {
        WaterRecordVO vo = new WaterRecordVO();
        BeanUtils.copyProperties(record, vo);
        Station station = stationService.getStationById(record.getStationId());
        if (station != null) {
            vo.setStationName(station.getName());
        }
        return vo;
    }
}
