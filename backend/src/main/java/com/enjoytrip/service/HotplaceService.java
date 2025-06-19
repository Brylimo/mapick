package com.enjoytrip.service;

import com.enjoytrip.model.dto.HotplaceDTO;

import java.util.List;

public interface HotplaceService {
    void addHotplace(HotplaceDTO hotplace);
    List<HotplaceDTO> getHotplace(int mno);
    boolean deleteHotplace(int mno, int hpid);
    HotplaceDTO getHotplaceById(Long hpid);
}
