package com.enjoytrip.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.enjoytrip.model.dto.TagDTO;

@Mapper
public interface TagDAO {
	List<TagDTO> getAllTags();
	void createTag(TagDTO tagDto);
	TagDTO findByName(String name);
}
