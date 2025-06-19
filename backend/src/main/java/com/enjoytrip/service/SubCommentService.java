package com.enjoytrip.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.enjoytrip.model.dto.CommentDTO;
import com.enjoytrip.model.dto.SubCommentDTO;


public interface SubCommentService {

	void insertSubComment(SubCommentDTO subCommentDto);

	SubCommentDTO getSubCommentById(int subCommentId);

	List<SubCommentDTO> getAllSubCommentsByCommentId(int commentId);

	void updateSubComment(SubCommentDTO subCommentDto);

	boolean deleteSubComment(int subCommentId);

	
}
