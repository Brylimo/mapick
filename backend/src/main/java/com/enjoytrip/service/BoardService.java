package com.enjoytrip.service;

import java.util.List;

import com.enjoytrip.domain.community.Board;
import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.board.BoardRequestDto;
import com.enjoytrip.dto.board.BoardResponseDto;
import com.enjoytrip.model.dto.BoardDTO;
import com.enjoytrip.model.dto.BoardResDTO;

public interface BoardService {
	void createBoard(Board board);
	BoardResponseDto.BoardInfo getBoardById(long id, int memberId);
	List<BoardDTO> getAllBoards();
	List<BoardResponseDto.BoardInfo> getBoardsByOpt(String opt, String query, Integer communityId, Member member);
	boolean boardDelete(long boardId);
	Board boardUpdate(BoardRequestDto.BoardUpdate boardUpdate);
	List<BoardResDTO> getBoardsByCommunityId(int communityId, int userId);
}
