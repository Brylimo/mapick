package com.enjoytrip.model.dao;

import com.enjoytrip.model.dto.LikeDTO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface LikeDAO {
    void insertLike(LikeDTO likeDTO);
    List<Map<String, Object>> getLikeCountsByBoardIds(@Param("boardIds") List<Long> boardIds);
    List<Long> getLikedByBoardIdAndUserId(List<Long> boardIds, int memberId);
    int getLikeCnt(long boardId);
    boolean getLiked(long boardId, int memberId);
	void deleteLike(LikeDTO likeDto);
    Integer getAllLikeCnt(int boardId);
}
