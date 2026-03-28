package com.example.watermonitor.service;

import com.example.watermonitor.dao.StationDao;
import com.example.watermonitor.model.pojo.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {
    
    @Autowired
    private StationDao stationDao;
    
    public List<Station> getAllStations() {
        return stationDao.findAll();
    }
    
    public Station getStationById(Long id) {
        return stationDao.findById(id);
    }
    
    public void addStation(Station station) {
        station.setStatus(1);
        stationDao.save(station);
    }
    
    public void updateStation(Station station) {
        stationDao.update(station);
    }
    
    public void disableStation(Long id) {
        stationDao.disable(id);
    }
}
