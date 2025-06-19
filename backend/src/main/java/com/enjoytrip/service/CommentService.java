package com.enjoytrip.service;

import java.util.List;

import com.enjoytrip.model.dto.CommentDTO;

public interface CommentService {

	void insertComment(CommentDTO commentDto);

	List<CommentDTO> getAllCommentsByBoardId(long boardId);

	void updateComment(CommentDTO commentDto);

	CommentDTO getCommentById(long commentId);

	boolean deleteComment(Long commentId);

}
