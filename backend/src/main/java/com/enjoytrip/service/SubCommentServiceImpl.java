package com.enjoytrip.service;

import java.util.List;

import com.enjoytrip.model.dao.SubCommentDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.enjoytrip.model.dto.SubCommentDTO;

@Service
@RequiredArgsConstructor
public class SubCommentServiceImpl implements SubCommentService {
	private final SubCommentDAO subCommentDAO;

	@Override
	public void insertSubComment(SubCommentDTO subCommentDto) {
		subCommentDAO.insertSubComment(subCommentDto);
	}

	@Override
	public SubCommentDTO getSubCommentById(int subCommentId) {
		return subCommentDAO.getSubCommentById(subCommentId);
	}

	@Override
	public List<SubCommentDTO> getAllSubCommentsByCommentId(int commentId) {
		return subCommentDAO.getAllSubCommentByCommentId(commentId);
	}

	@Override
	public void updateSubComment(SubCommentDTO subCommentDto) {
		subCommentDAO.updateSubComment(subCommentDto);
	}

	@Override
	public boolean deleteSubComment(int subCommentId) {
		SubCommentDTO subComment = subCommentDAO.getSubCommentById(subCommentId);
		if(subComment!=null){
			subCommentDAO.deleteSubComment(subCommentId);
			return true;
		}
		return false;
	}
	
}
