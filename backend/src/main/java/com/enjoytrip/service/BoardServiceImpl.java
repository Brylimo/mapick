package com.enjoytrip.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.enjoytrip.domain.community.Board;
import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.board.BoardRequestDto;
import com.enjoytrip.dto.board.BoardResponseDto;
import com.enjoytrip.model.dto.BoardResDTO;
import com.enjoytrip.util.KmpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enjoytrip.model.dao.BoardDAO;
import com.enjoytrip.model.dto.BoardDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {
	private final BoardDAO boardDao;
	private final KmpUtil kmpUtil;
	private final LikeService likeService;

	@Override
	@Transactional
	public void createBoard(Board board) {
		boardDao.insertBoard(board);
	}

	@Override
	@Transactional
	public BoardResponseDto.BoardInfo getBoardById(long id, int memberId) {
		Board board = boardDao.getBoardById(id);
		BoardResponseDto.BoardInfo res = BoardResponseDto.BoardInfo.builder()
				.id(board.getId())
				.title(board.getTitle())
				.content(board.getContent())
				.createdAt(board.getCreatedAt())
				.updatedAt(board.getUpdatedAt())
				.authorName(board.getMember().getUsername())
				.authorId(board.getMember().getMno())
				.build();
		res = likeService.getLike(res, memberId);
		return res;
	}

	@Override
	@Transactional
	public List<BoardDTO> getAllBoards() {
		return boardDao.getAllBoards();
	}

	@Override
	@Transactional
	public List<BoardResponseDto.BoardInfo> getBoardsByOpt(String opt, String query, Integer communityId, Member member) {
		List<Board> boards = boardDao.getBoardsByOpt(opt, query, communityId);

		if (opt.equals("name")) {
			boards.sort((o1, o2) -> {
				int cnt1 = KmpUtil.countMatches(o1.getTitle(), query);
				int cnt2 = KmpUtil.countMatches(o2.getTitle(), query);
				return Integer.compare(cnt2, cnt1);
			});
		} else if (opt.equals("name_content")) {
			boards.sort((o1, o2) -> {
				int cnt1 = KmpUtil.countMatches(o1.getTitle(), query) + KmpUtil.countMatches(o1.getContent(), query);
				int cnt2 = KmpUtil.countMatches(o2.getTitle(), query) + KmpUtil.countMatches(o2.getContent(), query);
				return Integer.compare(cnt2, cnt1);
			});
		} else if (opt.equals("writer")) {
			boards.sort((o1, o2) -> {
				int cnt1 = KmpUtil.countMatches(o1.getTitle(), query);
				int cnt2 = KmpUtil.countMatches(o2.getTitle(), query);
				return Integer.compare(cnt2, cnt1);
			});
		}

		List<BoardResponseDto.BoardInfo> list = new ArrayList<>();
		if(boards.size()>0) {
			list = likeService.getAllLikes(boards, member);
		}

		return list;
	}

	@Override
	@Transactional
	public boolean boardDelete(long boardId) {
		Board board = boardDao.getBoardById(boardId);
		if (board != null) {
			boardDao.deleteBoard(boardId);
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public Board boardUpdate(BoardRequestDto.BoardUpdate boardUpdate) {
		boardDao.updateBoard(boardUpdate);
		return boardDao.getBoardById(boardUpdate.getBoardId());
	}

	@Override
	@Transactional
	public List<BoardResDTO> getBoardsByCommunityId(int communityId, int userId) {
		List<BoardDTO> list = boardDao.getBoardsByCommunityId(communityId);
		List<BoardResDTO> resList = list.stream()
				.map(dto -> BoardResDTO.builder()
						.id(dto.getId())
						.title(dto.getTitle())
						.content(dto.getContent())
						.createdAt(dto.getCreatedAt())
						.updatedAt(dto.getUpdatedAt())
						.author(dto.getAuthor())
						.member(dto.getMember())
						.build())
				.collect(Collectors.toList());
		/*if(resList.size()>0) {
			resList = likeService.getAllLikes(resList, userId);
		}*/
		return resList;
	}



}
