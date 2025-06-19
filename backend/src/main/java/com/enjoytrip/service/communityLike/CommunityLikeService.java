package com.enjoytrip.service.communityLike;

import com.enjoytrip.domain.community.CommunityLike;
import com.enjoytrip.dto.community.CommunityLikeRequestDto;

public interface CommunityLikeService {
    void createCommunityLike(CommunityLike communityLike);
    boolean checkCommunityLike(CommunityLike communityLike);
    void deleteCommunityLike(CommunityLike communityLike);
}
