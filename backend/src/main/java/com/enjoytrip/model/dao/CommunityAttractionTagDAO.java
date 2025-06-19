package com.enjoytrip.model.dao;

import com.enjoytrip.domain.community.CommunityAttractionTag;
import com.enjoytrip.model.dto.CommunityAttractionTagDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommunityAttractionTagDAO {
    void insertCommunityAttractionTag(CommunityAttractionTagDTO communityAttractionTagDTO);
    List<CommunityAttractionTag> getCommunityAttractionTags(Integer communityAttractionId);
}
