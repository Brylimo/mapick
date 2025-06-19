package com.enjoytrip.service;

import com.enjoytrip.model.dao.SidosDAO;
import com.enjoytrip.model.dto.SidosDTO;
import com.enjoytrip.model.dto.SidosDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SidosServiceImpl implements SidosService {
    private final SidosDAO sidosDAO;

    @Override
    @Transactional
    public List<SidosDTO> getAllSidos() {
        return sidosDAO.getAllSidos();
    }

    @Override
    @Transactional
    public void insertSidoList(List<SidosDataDTO> list) {
        for (SidosDataDTO sido : list) {
            sidosDAO.insertSidoOne(sido);
        }
    }
}
