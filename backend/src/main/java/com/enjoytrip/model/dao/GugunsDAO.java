package com.enjoytrip.model.dao;

import com.enjoytrip.model.dto.GugunsDTO;
import com.enjoytrip.model.dto.GugunsDataDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GugunsDAO {
    List<GugunsDTO> getGugunsBySido(int sidoCode);
    void insertGugunOne(GugunsDataDTO gugun);
}
