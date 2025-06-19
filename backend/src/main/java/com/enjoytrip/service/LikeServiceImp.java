package com.enjoytrip.service;

import com.enjoytrip.domain.community.Board;
import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.board.BoardResponseDto;
import com.enjoytrip.model.dao.LikeDAO;
import com.enjoytrip.model.dto.BoardResDTO;
import com.enjoytrip.model.dto.LikeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeServiceImp implements LikeService{
    private final LikeDAO likeDao;

    @Override
    @Transactional
    public void insertLike(LikeDTO likeDTO){
        likeDao.insertLike(likeDTO);
    }

    @Override
    @Transactional
    public List<BoardResponseDto.BoardInfo> getAllLikes(List<Board> list, Member member) {
        List<BoardResponseDto.BoardInfo> res = new ArrayList<>();

        List<Long> boardIds = list.stream().map(Board::getId).collect(Collectors.toList());

        // 좋아요 수 가지고 오기
        List<Map<String, Object>> likeCnts = likeDao.getLikeCountsByBoardIds(boardIds);
        Map<Long, Integer> likeMap = new HashMap<>();
        for (Map<String, Object> row : likeCnts) {
            Long boardId = ((Number) row.get("board_id")).longValue();
            Integer likeCnt = ((Number) row.get("like_cnt")).intValue();
            likeMap.put(boardId, likeCnt);
        }

        // 현재 유저가 좋아요를 눌렀는지 가지고 오기
        List<Long> likedStatus = likeDao.getLikedByBoardIdAndUserId(boardIds, member.getMno());
        for(Board board : list){
            res.add(BoardResponseDto.BoardInfo.builder()
                            .id(board.getId())
                            .title(board.getTitle())
                            .content(board.getContent())
                            .authorName(board.getMember().getUsername())
                            .authorId(board.getMember().getMno())
                            .createdAt(board.getCreatedAt())
                            .updatedAt(board.getUpdatedAt())
                            .likeCnt(likeMap.getOrDefault(board.getId(), 0))
                            .liked(likedStatus.contains(board.getId()))
                            .build());
        }

        return res;
    }

    @Override
    @Transactional
    public BoardResponseDto.BoardInfo getLike(BoardResponseDto.BoardInfo res, int userId) {
        res.setLikeCnt(likeDao.getLikeCnt(res.getId()));
        res.setLiked(likeDao.getLiked(res.getId(), userId));
        return res;
    }

    @Override
    @Transactional
    public boolean checkLiked(int id, int mno) {
        return likeDao.getLiked(id, mno);
    }

    @Override
    @Transactional
    public void deleteLike(LikeDTO likeDto) {
        likeDao.deleteLike(likeDto);
    }

    @Override
    public Integer getAllLikeCnt(int id) {
        return likeDao.getAllLikeCnt(id);
    }
}
