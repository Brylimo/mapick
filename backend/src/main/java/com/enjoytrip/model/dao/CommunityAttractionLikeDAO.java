package com.enjoytrip.model.dao;

import com.enjoytrip.domain.community.CommunityAttractionLike;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommunityAttractionLikeDAO {
    void insertCommunityAttractionLike(CommunityAttractionLike communityAttractionLike);
    Integer communityAttractionLikeCount(CommunityAttractionLike communityAttractionLike);
    void deleteCommunityAttractionLike(CommunityAttractionLike communityAttractionLike);
    int getCommunityAttractionLikeCount(int communityAttractionId);
    boolean getCommunityAttractionLiked(int communityAttractionId, int memberId);
}
