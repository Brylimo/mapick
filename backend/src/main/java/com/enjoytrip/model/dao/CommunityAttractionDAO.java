package com.enjoytrip.model.dao;

import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.community.CommunityAttractionResponseDto;
import com.enjoytrip.model.dto.CommunityAttractionDTO;
import com.enjoytrip.model.dto.PopularAttractionDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommunityAttractionDAO {
    void insertCommunityAttraction(CommunityAttractionDTO communityAttractionDTO);
    List<CommunityAttractionDTO> findAllCommunityAttractions(Integer communityId, String query, List<String> tags, Member member);
    List<PopularAttractionDTO> findPopularAttractions();
}