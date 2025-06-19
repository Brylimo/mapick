package com.enjoytrip.service.communityAttractionLike;

import com.enjoytrip.domain.community.CommunityAttractionLike;
import com.enjoytrip.domain.community.CommunityLike;
import com.enjoytrip.model.dto.CommunityAttractionDTO;

public interface CommunityAttractionLikeService {
    void createCommunityAttractionLike(CommunityAttractionLike communityAttractionLike);
    boolean checkCommunityAttractionLike(CommunityAttractionLike communityAttractionLike);
    void deleteCommunityAttractionLike(CommunityAttractionLike communityAttractionLike);
    CommunityAttractionDTO getCommunityAttractionLike(CommunityAttractionDTO res, Integer memberId);
}
