package com.enjoytrip.service;

import com.enjoytrip.model.dao.AttractionDAO;
import com.enjoytrip.model.dto.AttractionDTO;
import com.enjoytrip.model.dto.AttractionDataDTO;
import com.enjoytrip.util.TourUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttractionServiceImpl implements AttractionService {
    private final AttractionDAO attractionDAO;
    private final TourUtil tourUtil;

    @Override
    @Transactional
    public List<AttractionDataDTO> getAttractionsByAttractionDTO(AttractionDataDTO attractionDTO) {
        List<AttractionDataDTO> list = tourUtil.searchAttractionsByAttractionDTO(attractionDTO);
        return list;
    }

    @Override
    @Transactional
    public void createAttraction(AttractionDataDTO attraction) {
        attractionDAO.insertAttraction(attraction);
    }

    @Override
    @Transactional
    public void createAttractionCustom(AttractionDataDTO attraction) {
        attractionDAO.insertAttractionCustom(attraction);
    }

    @Override
    @Transactional
    public AttractionDTO getAttraction(String title, double latitude, double longitude) {
        return attractionDAO.getAttraction(title, latitude, longitude);
    }
}
