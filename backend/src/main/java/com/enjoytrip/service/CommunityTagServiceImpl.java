package com.enjoytrip.service;

import com.enjoytrip.model.dao.CommunityTagDAO;
import com.enjoytrip.model.dto.CommunityTagDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityTagServiceImpl implements CommunityTagService {
    private final CommunityTagDAO communityTagDAO;

    @Override
    @Transactional
    public CommunityTagDTO createCommunityTag(CommunityTagDTO communityTagDTO) {
        try {
            communityTagDAO.insertCommunityTag(communityTagDTO);
            return communityTagDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
