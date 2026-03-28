package com.example.watermonitor.mapper;

import com.example.watermonitor.model.pojo.AlertRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlertRecordMapper {
    int insert(AlertRecord record);
    
    List<AlertRecord> findAll(@Param("status") Integer status);
    
    AlertRecord findById(Long id);
    
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
