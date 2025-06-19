package com.enjoytrip.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.enjoytrip.model.dto.CategoryDTO;
import com.enjoytrip.model.dto.CommunityCategoryDTO;
import com.enjoytrip.model.dto.CommunityDTO;


@Mapper
public interface CommunityCategoryDAO {
	void insertCommunityCategory(CommunityCategoryDTO communityCategoryDTO);
	List<CategoryDTO> findByCommunityId(int communityId);
	List<CommunityDTO> findByCategoryId(int categoryId);
	void deleteByCommunityId(int communityId);
}
