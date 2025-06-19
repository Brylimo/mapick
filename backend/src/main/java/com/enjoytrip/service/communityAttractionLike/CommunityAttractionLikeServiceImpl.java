package com.enjoytrip.service.communityAttractionLike;

import com.enjoytrip.domain.community.CommunityAttractionLike;
import com.enjoytrip.model.dao.CommunityAttractionLikeDAO;
import com.enjoytrip.model.dto.CommunityAttractionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityAttractionLikeServiceImpl implements CommunityAttractionLikeService {
    private final CommunityAttractionLikeDAO communityAttractionLikeDAO;

    @Override
    @Transactional
    public void createCommunityAttractionLike(CommunityAttractionLike communityAttractionLike) {
        communityAttractionLikeDAO.insertCommunityAttractionLike(communityAttractionLike);
    }

    @Override
    @Transactional
    public boolean checkCommunityAttractionLike(CommunityAttractionLike communityAttractionLike) {
        Integer count = communityAttractionLikeDAO.communityAttractionLikeCount(communityAttractionLike);

        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public void deleteCommunityAttractionLike(CommunityAttractionLike communityAttractionLike) {
        communityAttractionLikeDAO.deleteCommunityAttractionLike(communityAttractionLike);
    }

    @Override
    @Transactional
    public CommunityAttractionDTO getCommunityAttractionLike(CommunityAttractionDTO res, Integer memberId) {
        res.setLikeCnt(communityAttractionLikeDAO.getCommunityAttractionLikeCount(res.getCommunityAttractionId()));
        res.setLiked(communityAttractionLikeDAO.getCommunityAttractionLiked(res.getCommunityAttractionId(), memberId));
        return res;
    }
}
