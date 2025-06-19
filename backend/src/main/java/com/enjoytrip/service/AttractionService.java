package com.enjoytrip.service;

import com.enjoytrip.model.dto.AttractionDTO;
import com.enjoytrip.model.dto.AttractionDataDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttractionService {
    List<AttractionDataDTO> getAttractionsByAttractionDTO(AttractionDataDTO attractionDTO);
    void createAttraction(AttractionDataDTO attraction);
    void createAttractionCustom(AttractionDataDTO attraction);
    AttractionDTO getAttraction(String title, double latitude, double longitude);
}
