package com.enjoytrip.service.communityLike;

import com.enjoytrip.domain.community.CommunityLike;
import com.enjoytrip.model.dao.CommunityLikeDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommunityLikeServiceImpl implements CommunityLikeService {
    private final CommunityLikeDAO communityLikeDAO;

    @Override
    public void createCommunityLike(CommunityLike communityLike) {
        communityLikeDAO.insertCommunityLike(communityLike);
    }

    @Override
    public boolean checkCommunityLike(CommunityLike communityLike) {
        Integer count = communityLikeDAO.communityLikeCount(communityLike);

        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void deleteCommunityLike(CommunityLike communityLike) {
        communityLikeDAO.deleteCommunityLike(communityLike);
    }
}
