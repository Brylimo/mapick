package com.enjoytrip.model.dao;

import java.util.List;

import com.enjoytrip.domain.community.Board;
import com.enjoytrip.dto.board.BoardRequestDto;
import com.enjoytrip.dto.board.BoardResponseDto;
import org.apache.ibatis.annotations.Mapper;

import com.enjoytrip.model.dto.BoardDTO;

@Mapper
public interface BoardDAO {
	void insertBoard(Board board);
	Board getBoardById(long id);
	List<BoardDTO> getAllBoards();
	List<Board> getBoardsByOpt(String opt, String query, Integer communityId);
	void updateBoard(BoardRequestDto.BoardUpdate boardUpdate);
	void deleteBoard(long id);
	List<BoardDTO> getBoardsByCommunityId(int communityId);
}
