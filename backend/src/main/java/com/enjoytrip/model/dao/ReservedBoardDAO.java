package com.enjoytrip.model.dao;

import com.enjoytrip.model.dto.BoardDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReservedBoardDAO {
    void insertReservedBoard(BoardDTO reservedBoard);
    BoardDTO getReservedBoardById(long id);
    List<BoardDTO> getAllReservedBoards();
    void deleteReservedBoard(long id);
}
