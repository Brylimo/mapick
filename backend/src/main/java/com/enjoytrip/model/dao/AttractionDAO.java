package com.enjoytrip.model.dao;

import com.enjoytrip.model.dto.AttractionDTO;
import com.enjoytrip.model.dto.AttractionDataDTO;
import org.apache.ibatis.annotations.Mapper;

import java.sql.SQLException;
import java.util.List;

@Mapper
public interface AttractionDAO {
    List<AttractionDTO> getAttractionsByAttractionDTO(AttractionDTO attractionDTO);
    void insertAttraction(AttractionDataDTO attraction);
    void insertAttractionCustom(AttractionDataDTO attraction);
    AttractionDTO getAttraction(String title, double latitude, double longitude);
}
