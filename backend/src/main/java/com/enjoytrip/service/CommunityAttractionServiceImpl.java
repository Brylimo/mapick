package com.enjoytrip.service;

import com.enjoytrip.domain.community.CommunityAttractionTag;
import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.community.CommunityAttractionResponseDto;
import com.enjoytrip.model.dao.CommunityAttractionDAO;
import com.enjoytrip.model.dao.CommunityAttractionTagDAO;
import com.enjoytrip.model.dto.CommunityAttractionDTO;
import com.enjoytrip.model.dto.PopularAttractionDTO;
import com.enjoytrip.model.dto.TagDTO;
import com.enjoytrip.service.communityAttractionLike.CommunityAttractionLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityAttractionServiceImpl implements CommunityAttractionService {
    private final CommunityAttractionDAO communityAttractionDAO;
    private final CommunityAttractionTagDAO communityAttractionTagDAO;

    private final CommunityAttractionLikeService communityAttractionLikeService;

    @Override
    @Transactional
    public CommunityAttractionDTO createCommunityAttraction(CommunityAttractionDTO communityAttractionDTO) {
        try {
            communityAttractionDAO.insertCommunityAttraction(communityAttractionDTO);
            return communityAttractionDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional
    public List<CommunityAttractionDTO> findAllCommunityAttractions(Integer communityId, String query, List<String> tags, Member member) {
        List<CommunityAttractionDTO> list = communityAttractionDAO.findAllCommunityAttractions(communityId, query, tags, member);

        for (CommunityAttractionDTO dto : list) {
            List<CommunityAttractionTag> communityAttractionTags = communityAttractionTagDAO.getCommunityAttractionTags(dto.getCommunityAttractionId());

            List<TagDTO> tagList = communityAttractionTags.stream().map(communityAttractionTag -> communityAttractionTag.getTag()).collect(Collectors.toList());
            dto.setTags(tagList);

            dto = communityAttractionLikeService.getCommunityAttractionLike(dto, member.getMno());
        }

        return list;
    }

    @Override
    @Transactional
    public List<PopularAttractionDTO> getPopularAttractions() {
        return communityAttractionDAO.findPopularAttractions();
    }
}
