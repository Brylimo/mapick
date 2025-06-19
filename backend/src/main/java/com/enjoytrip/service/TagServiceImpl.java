package com.enjoytrip.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enjoytrip.model.dao.CommunityTagDAO;
import com.enjoytrip.model.dao.TagDAO;
import com.enjoytrip.model.dto.TagDTO;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
	private final TagDAO tagDao;
	private final CommunityTagDAO communityTagDao;
	@Override
	@Transactional
	public List<TagDTO> getAllTags() {
		return tagDao.getAllTags();
	}

	@Override
	@Transactional
	public TagDTO createTag(String tagName) {
		TagDTO tagDto = new TagDTO();
		tagDto.setName(tagName);
		tagDao.createTag(tagDto);

		return tagDto;
	}

	@Override
	@Transactional
	public TagDTO findByName(String name) {
		TagDTO tagDto = tagDao.findByName(name);
		return tagDto;
	}
	
	@Override
	@Transactional
	public List<TagDTO> findByCommunityId(int communityId) {
		List<TagDTO> list = communityTagDao.findByCommunityId(communityId);
		return list;
	}

}
