package com.enjoytrip.service;

import com.enjoytrip.domain.community.Board;
import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.board.BoardResponseDto;
import com.enjoytrip.model.dto.BoardResDTO;
import com.enjoytrip.model.dto.LikeDTO;

import java.util.List;

public interface LikeService {
    void insertLike(LikeDTO likeDTO);

    List<BoardResponseDto.BoardInfo> getAllLikes(List<Board> list, Member member);

    BoardResponseDto.BoardInfo getLike(BoardResponseDto.BoardInfo res, int userId);

	boolean checkLiked(int id, int mno);

	void deleteLike(LikeDTO likeDTO);

    Integer getAllLikeCnt(int id);
}
