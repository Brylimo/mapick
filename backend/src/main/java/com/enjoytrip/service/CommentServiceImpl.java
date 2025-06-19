package com.enjoytrip.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enjoytrip.model.dao.CommentDAO;
import com.enjoytrip.model.dto.CommentDTO;
import com.enjoytrip.model.dto.NoticeDTO;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
	private final CommentDAO commentDao;
	
	@Override
	@Transactional
	public void insertComment(CommentDTO commentDto) {
		commentDao.insertComment(commentDto);
	}

	@Override
	@Transactional
	public List<CommentDTO> getAllCommentsByBoardId(long boardId) {
		return commentDao.getAllCommentsByBoardId(boardId);
	}

	@Override
	@Transactional
	public void updateComment(CommentDTO commentDto) {
		commentDao.updateComment(commentDto);
	}

	@Override
	@Transactional
	public CommentDTO getCommentById(long commentId) {
		return commentDao.getCommentById(commentId);
	}

	@Override
	@Transactional
	public boolean deleteComment(Long commentId) {
		CommentDTO comment = commentDao.getCommentById(commentId);
		if (comment != null) {
			commentDao.deleteComment(commentId);
			return true;
		}
		return false;
	}

}
