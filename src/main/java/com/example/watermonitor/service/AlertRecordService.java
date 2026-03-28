package com.example.watermonitor.service;

import com.example.watermonitor.dao.AlertRecordDao;
import com.example.watermonitor.model.pojo.AlertRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertRecordService {
    
    @Autowired
    private AlertRecordDao alertRecordDao;
    
    public List<AlertRecord> getAlerts(Integer status) {
        return alertRecordDao.findAll(status);
    }
    
    public void handleAlert(Long id) {
        AlertRecord alert = alertRecordDao.findById(id);
        if (alert == null) {
            throw new RuntimeException("警报记录不存在");
        }
        if (alert.getStatus() == 1) {
            throw new RuntimeException("该警报已处理");
        }
        alertRecordDao.handle(id);
    }
}
