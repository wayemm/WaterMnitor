package com.example.watermonitor.controller;

import com.example.watermonitor.common.Result;
import com.example.watermonitor.model.pojo.Station;
import com.example.watermonitor.model.vo.StationVO;
import com.example.watermonitor.service.StationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stations")
public class StationController {
    
    @Autowired
    private StationService stationService;
    
    @GetMapping
    public Result<List<StationVO>> getAllStations() {
        List<Station> stations = stationService.getAllStations();
        List<StationVO> voList = stations.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }
    
    @GetMapping("/{id}")
    public Result<StationVO> getStationById(@PathVariable Long id) {
        Station station = stationService.getStationById(id);
        if (station == null) {
            return Result.error("站点不存在");
        }
        return Result.success(convertToVO(station));
    }
    
    @PostMapping
    public Result<Void> addStation(@RequestBody Station station) {
        stationService.addStation(station);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> updateStation(@PathVariable Long id, @RequestBody Station station) {
        station.setId(id);
        stationService.updateStation(station);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteStation(@PathVariable Long id) {
        stationService.disableStation(id);
        return Result.success();
    }
    
    private StationVO convertToVO(Station station) {
        StationVO vo = new StationVO();
        BeanUtils.copyProperties(station, vo);
        return vo;
    }
}
