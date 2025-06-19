package com.enjoytrip.service;

import java.util.List;

import com.enjoytrip.model.dto.CategoryDTO;
import com.enjoytrip.model.dto.CommunityDTO;

public interface CommunityService {

	void createCommunity(CommunityDTO communityDto);

	List<CommunityDTO> getCommunitiesByCategory(String categoryName);

	List<CommunityDTO> getCommunitiesByTag(String tagName);

	List<CommunityDTO> getAllCommunities();

	CommunityDTO getCommunitiesByCommunityName(String communityName);

	CommunityDTO getCommunityById(int communityId);

	void updateCommunity(CommunityDTO communityDto);

	void communityDelete(int communityId);

	List<CommunityDTO> getTop3PopularCommunities();
}
