package com.enjoytrip.service;

import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.community.CommunityAttractionResponseDto;
import com.enjoytrip.model.dto.AttractionDTO;
import com.enjoytrip.model.dto.CommunityAttractionDTO;
import com.enjoytrip.model.dto.PopularAttractionDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommunityAttractionService {
    CommunityAttractionDTO createCommunityAttraction(CommunityAttractionDTO communityAttractionDTO);
    List<CommunityAttractionDTO> findAllCommunityAttractions(Integer communityId, String query, List<String> tags, Member member);
    List<PopularAttractionDTO> getPopularAttractions();
}
