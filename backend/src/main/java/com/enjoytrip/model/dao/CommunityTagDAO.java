package com.enjoytrip.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.enjoytrip.model.dto.CommunityDTO;
import com.enjoytrip.model.dto.CommunityTagDTO;
import com.enjoytrip.model.dto.TagDTO;

@Mapper
public interface CommunityTagDAO {
	void insertCommunityTag(CommunityTagDTO communityTagDTO);
	List<TagDTO> findByCommunityId(int communityId);
	List<CommunityDTO> findByTagId(int tagId);
	void deleteByCommunityId(int communityId);
	
}
