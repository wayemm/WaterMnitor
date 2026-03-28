package com.example.watermonitor.mapper;

import com.example.watermonitor.model.pojo.WaterRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface WaterRecordMapper {
    int insert(WaterRecord record);
    
    List<WaterRecord> findByStationIdAndTimeRange(@Param("stationId") Long stationId,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);
    
    WaterRecord findLatestByStationId(Long stationId);
}
