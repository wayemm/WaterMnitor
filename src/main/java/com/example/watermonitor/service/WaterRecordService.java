package com.example.watermonitor.service;

import com.example.watermonitor.dao.AlertRecordDao;
import com.example.watermonitor.dao.StationDao;
import com.example.watermonitor.dao.WaterRecordDao;
import com.example.watermonitor.model.consts.AlertType;
import com.example.watermonitor.model.dto.WaterReportDTO;
import com.example.watermonitor.model.pojo.AlertRecord;
import com.example.watermonitor.model.pojo.Station;
import com.example.watermonitor.model.pojo.WaterRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WaterRecordService {
    
    @Autowired
    private WaterRecordDao waterRecordDao;
    
    @Autowired
    private StationDao stationDao;
    
    @Autowired
    private AlertRecordDao alertRecordDao;
    
    @Transactional
    public void reportWaterLevel(WaterReportDTO dto) {
        // 1. 校验站点是否存在
        Station station = stationDao.findById(dto.getStationId());
        if (station == null) {
            throw new RuntimeException("监测站不存在");
        }
        if (station.getStatus() != 1) {
            throw new RuntimeException("监测站已停用");
        }
        
        // 2. 保存水位记录
        WaterRecord record = new WaterRecord();
        record.setStationId(dto.getStationId());
        record.setWaterLevel(dto.getWaterLevel());
        record.setRecordedAt(dto.getRecordedAt() != null ? dto.getRecordedAt() : LocalDateTime.now());
        waterRecordDao.save(record);
        
        // 3. 判断是否触发警报
        BigDecimal waterLevel = dto.getWaterLevel();
        BigDecimal dangerLevel = station.getDangerLevel();
        BigDecimal warnLevel = station.getWarnLevel();
        
        AlertRecord alertRecord = null;
        
        if (waterLevel.compareTo(dangerLevel) >= 0) {
            // 危险警报
            alertRecord = new AlertRecord();
            alertRecord.setStationId(station.getId());
            alertRecord.setRecordId(record.getId());
            alertRecord.setAlertType(AlertType.DANGER.getCode());
            alertRecord.setWaterLevel(waterLevel);
            alertRecord.setStatus(0);
        } else if (waterLevel.compareTo(warnLevel) >= 0) {
            // 警戒警报
            alertRecord = new AlertRecord();
            alertRecord.setStationId(station.getId());
            alertRecord.setRecordId(record.getId());
            alertRecord.setAlertType(AlertType.WARNING.getCode());
            alertRecord.setWaterLevel(waterLevel);
            alertRecord.setStatus(0);
        }
        
        // 4. 创建警报记录（如果需要）
        if (alertRecord != null) {
            alertRecordDao.save(alertRecord);
        }
    }
    
    public List<WaterRecord> getRecords(Long stationId, LocalDateTime startTime, LocalDateTime endTime) {
        return waterRecordDao.findByStationIdAndTimeRange(stationId, startTime, endTime);
    }
    
    public WaterRecord getLatestRecord(Long stationId) {
        return waterRecordDao.findLatestByStationId(stationId);
    }
}
