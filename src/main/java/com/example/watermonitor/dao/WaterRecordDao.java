package com.example.watermonitor.dao;

import com.example.watermonitor.mapper.WaterRecordMapper;
import com.example.watermonitor.model.pojo.WaterRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class WaterRecordDao {
    
    @Autowired
    private WaterRecordMapper waterRecordMapper;
    
    public int save(WaterRecord record) {
        return waterRecordMapper.insert(record);
    }
    
    public List<WaterRecord> findByStationIdAndTimeRange(Long stationId, LocalDateTime startTime, LocalDateTime endTime) {
        return waterRecordMapper.findByStationIdAndTimeRange(stationId, startTime, endTime);
    }
    
    public WaterRecord findLatestByStationId(Long stationId) {
        return waterRecordMapper.findLatestByStationId(stationId);
    }
}
