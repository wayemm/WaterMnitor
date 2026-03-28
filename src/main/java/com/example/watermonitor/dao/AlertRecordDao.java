package com.example.watermonitor.dao;

import com.example.watermonitor.mapper.AlertRecordMapper;
import com.example.watermonitor.model.pojo.AlertRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AlertRecordDao {
    
    @Autowired
    private AlertRecordMapper alertRecordMapper;
    
    public int save(AlertRecord record) {
        return alertRecordMapper.insert(record);
    }
    
    public List<AlertRecord> findAll(Integer status) {
        return alertRecordMapper.findAll(status);
    }
    
    public AlertRecord findById(Long id) {
        return alertRecordMapper.findById(id);
    }
    
    public int handle(Long id) {
        return alertRecordMapper.updateStatus(id, 1);
    }
}
