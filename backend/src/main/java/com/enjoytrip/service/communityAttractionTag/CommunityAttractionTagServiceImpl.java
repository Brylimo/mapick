package com.enjoytrip.service.communityAttractionTag;

import com.enjoytrip.domain.community.CommunityAttractionTag;
import com.enjoytrip.model.dao.CommunityAttractionTagDAO;
import com.enjoytrip.model.dto.CommunityAttractionTagDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityAttractionTagServiceImpl implements CommunityAttractionTagService {
    private final CommunityAttractionTagDAO communityAttractionTagDAO;

    @Override
    public List<CommunityAttractionTag> getCommunityAttractionTagList(Integer communityAttractionId) {
        return communityAttractionTagDAO.getCommunityAttractionTags(communityAttractionId);
    }

    @Override
    @Transactional
    public CommunityAttractionTagDTO createCommunityAttractionTag(CommunityAttractionTagDTO communityAttractionTagDTO) {
        try {
            communityAttractionTagDAO.insertCommunityAttractionTag(communityAttractionTagDTO);
            return communityAttractionTagDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
