package com.example.watermonitor.controller;

import com.example.watermonitor.common.Result;
import com.example.watermonitor.model.consts.AlertStatus;
import com.example.watermonitor.model.consts.AlertType;
import com.example.watermonitor.model.pojo.AlertRecord;
import com.example.watermonitor.model.pojo.Station;
import com.example.watermonitor.model.vo.AlertRecordVO;
import com.example.watermonitor.service.AlertRecordService;
import com.example.watermonitor.service.StationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/alerts")
public class AlertRecordController {
    
    @Autowired
    private AlertRecordService alertRecordService;
    
    @Autowired
    private StationService stationService;
    
    @GetMapping
    public Result<List<AlertRecordVO>> getAlerts(@RequestParam(required = false) Integer status) {
        List<AlertRecord> records = alertRecordService.getAlerts(status);
        List<AlertRecordVO> voList = records.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }
    
    @PutMapping("/{id}/handle")
    public Result<Void> handleAlert(@PathVariable Long id) {
        alertRecordService.handleAlert(id);
        return Result.success();
    }
    
    private AlertRecordVO convertToVO(AlertRecord record) {
        AlertRecordVO vo = new AlertRecordVO();
        BeanUtils.copyProperties(record, vo);
        
        Station station = stationService.getStationById(record.getStationId());
        if (station != null) {
            vo.setStationName(station.getName());
        }
        
        AlertType alertType = AlertType.of(record.getAlertType());
        if (alertType != null) {
            vo.setAlertTypeDesc(alertType.getDesc());
        }
        
        AlertStatus alertStatus = AlertStatus.of(record.getStatus());
        if (alertStatus != null) {
            vo.setStatusDesc(alertStatus.getDesc());
        }
        
        return vo;
    }
}
