package com.example.watermonitor.dao;

import com.example.watermonitor.mapper.StationMapper;
import com.example.watermonitor.model.pojo.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StationDao {
    
    @Autowired
    private StationMapper stationMapper;
    
    public List<Station> findAll() {
        return stationMapper.findAll();
    }
    
    public Station findById(Long id) {
        return stationMapper.findById(id);
    }
    
    public int save(Station station) {
        return stationMapper.insert(station);
    }
    
    public int update(Station station) {
        return stationMapper.update(station);
    }
    
    public int disable(Long id) {
        return stationMapper.updateStatus(id, 0);
    }
}
