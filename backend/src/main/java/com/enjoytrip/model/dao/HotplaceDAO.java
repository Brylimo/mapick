package com.enjoytrip.model.dao;

import com.enjoytrip.model.dto.HotplaceDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HotplaceDAO {
    void insertHotplace(HotplaceDTO hotplace);
    List<HotplaceDTO> getHotplace(int mno);
    int getAttractionId(String title);
    boolean deleteHotplace(int mno, int hpid);
    HotplaceDTO getHotplaceById(Long hpid);
}
