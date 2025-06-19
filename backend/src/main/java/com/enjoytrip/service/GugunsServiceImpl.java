package com.enjoytrip.service;

import com.enjoytrip.model.dao.GugunsDAO;
import com.enjoytrip.model.dto.GugunsDTO;
import com.enjoytrip.model.dto.GugunsDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GugunsServiceImpl implements GugunsService {
    private final GugunsDAO gugunsDAO;

    @Override
    @Transactional
    public void insertGugunList(List<GugunsDataDTO> list) {
        for (GugunsDataDTO gugun : list) {
            gugunsDAO.insertGugunOne(gugun);
        }
    }

    @Override
    @Transactional
    public List<GugunsDTO> getGugunsBySido(int sidoCode) {
        return gugunsDAO.getGugunsBySido(sidoCode);
    }
}
