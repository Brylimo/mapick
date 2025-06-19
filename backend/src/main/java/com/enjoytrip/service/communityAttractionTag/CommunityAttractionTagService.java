package com.enjoytrip.service.communityAttractionTag;

import com.enjoytrip.domain.community.CommunityAttractionTag;
import com.enjoytrip.model.dto.CommunityAttractionTagDTO;

import java.util.List;

public interface CommunityAttractionTagService {
    List<CommunityAttractionTag> getCommunityAttractionTagList(Integer communityAttractionId);
    CommunityAttractionTagDTO createCommunityAttractionTag(CommunityAttractionTagDTO communityAttractionTagDTO);
}
