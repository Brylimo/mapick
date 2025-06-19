package com.enjoytrip.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.enjoytrip.model.dto.SubCommentDTO;

@Mapper
public interface SubCommentDAO {
	void insertSubComment(SubCommentDTO subCommentDto);

	List<SubCommentDTO> getAllSubCommentByCommentId(long commentId);

	void updateSubComment(SubCommentDTO subCommentDto);

	SubCommentDTO getSubCommentById(long subCommentId);

	void deleteSubComment(int subCommentId);
}
