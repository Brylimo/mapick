package com.enjoytrip.service;

import com.enjoytrip.model.dto.BoardDTO;
import com.enjoytrip.domain.community.Board;

import java.util.List;

public interface ReservedBoardService {
    void createReservedBoard(BoardDTO reservedBoard);
    BoardDTO getReservedBoardById(long id);
    List<BoardDTO> getAllReservedBoards();
    boolean reservedBoardDelete(long reservedBoardId);
    void publishReservedPost(Board board);
}
