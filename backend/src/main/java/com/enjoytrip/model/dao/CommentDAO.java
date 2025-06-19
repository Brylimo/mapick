package com.enjoytrip.model.dao;

import java.util.List;

import com.enjoytrip.model.dto.CommunityDTO;
import org.apache.ibatis.annotations.Mapper;

import com.enjoytrip.model.dto.CommentDTO;

@Mapper
public interface CommentDAO {
	void insertComment(CommentDTO commentDto);

	List<CommentDTO> getAllCommentsByBoardId(long boardId);

	void updateComment(CommentDTO commentDto);

	CommentDTO getCommentById(long commentId);

	void deleteComment(Long commentId);

	List<CommunityDTO> getTop3CommunitiesWithTags();
}
