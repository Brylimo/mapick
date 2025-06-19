package com.enjoytrip.model.dao;

import com.enjoytrip.domain.community.CommunityLike;
import com.enjoytrip.dto.community.CommunityLikeRequestDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommunityLikeDAO {
    void insertCommunityLike(CommunityLike communityLike);
    Integer communityLikeCount(CommunityLike communityLike);
    void deleteCommunityLike(CommunityLike communityLike);
}
