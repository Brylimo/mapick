package com.enjoytrip.service;

import com.enjoytrip.model.dto.ContentTypeDTO;
import com.enjoytrip.model.dto.GugunsDTO;
import com.enjoytrip.model.dto.GugunsDataDTO;

import java.util.List;

public interface GugunsService {
    List<GugunsDTO> getGugunsBySido(int sidoCode);
    void insertGugunList(List<GugunsDataDTO> list);
}
