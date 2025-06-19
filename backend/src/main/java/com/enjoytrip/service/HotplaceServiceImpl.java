package com.enjoytrip.service;

import com.enjoytrip.model.dao.HotplaceDAO;
import com.enjoytrip.model.dto.HotplaceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotplaceServiceImpl implements HotplaceService {
    private final HotplaceDAO hotplaceDAO;

    @Override
    @Transactional
    public void addHotplace(HotplaceDTO hotplace) {
        hotplaceDAO.insertHotplace(hotplace);
    }

    @Override
    @Transactional
    public List<HotplaceDTO> getHotplace(int mno) {
        return hotplaceDAO.getHotplace(mno);
    }

    @Override
    @Transactional
    public boolean deleteHotplace(int mno, int hpid) {
        return hotplaceDAO.deleteHotplace(mno, hpid);
    }

    @Override
    public HotplaceDTO getHotplaceById(Long hpid) {
        return hotplaceDAO.getHotplaceById(hpid);
    }
}
