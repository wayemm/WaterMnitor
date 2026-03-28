package com.example.watermonitor.mapper;

import com.example.watermonitor.model.pojo.Station;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StationMapper {
    List<Station> findAll();
    
    Station findById(Long id);
    
    int insert(Station station);
    
    int update(Station station);
    
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
