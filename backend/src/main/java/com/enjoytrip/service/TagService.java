package com.enjoytrip.service;

import java.util.List;

import com.enjoytrip.model.dto.TagDTO;

public interface TagService {
	List<TagDTO> getAllTags();
	TagDTO createTag(String tagName);
	TagDTO findByName(String name);
	List<TagDTO> findByCommunityId(int communityId);
}
