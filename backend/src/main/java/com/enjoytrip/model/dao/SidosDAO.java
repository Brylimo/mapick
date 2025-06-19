package com.enjoytrip.model.dao;

import com.enjoytrip.model.dto.SidosDTO;
import com.enjoytrip.model.dto.SidosDataDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SidosDAO {
    List<SidosDTO> getAllSidos();
    void insertSidoOne(SidosDataDTO dto);
}
