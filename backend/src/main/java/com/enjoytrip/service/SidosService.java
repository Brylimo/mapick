package com.enjoytrip.service;

import com.enjoytrip.model.dto.SidosDTO;
import com.enjoytrip.model.dto.SidosDataDTO;

import java.util.List;

public interface SidosService {
    List<SidosDTO> getAllSidos();
    void insertSidoList(List<SidosDataDTO> list);
}
