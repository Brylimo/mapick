package com.enjoytrip.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.enjoytrip.model.dto.CommunityDTO;

@Mapper
public interface CommunityDAO {
	void insertCommunity(CommunityDTO communityDto);
	List<CommunityDTO> getAllCommunities();
	CommunityDTO findByName(String communityName);
	CommunityDTO findById(int communityId);
	void updateCommunity(CommunityDTO communityDto);
	void deleteById(int communityId);
	List<CommunityDTO> getTopLikedCommunities();
}
