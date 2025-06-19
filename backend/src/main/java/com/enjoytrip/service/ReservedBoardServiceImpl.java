package com.enjoytrip.service;

import com.enjoytrip.domain.community.Board;
import com.enjoytrip.model.dao.BoardDAO;
import com.enjoytrip.model.dao.ReservedBoardDAO;
import com.enjoytrip.model.dto.BoardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservedBoardServiceImpl implements ReservedBoardService {
    private final BoardDAO boardDAO;
    private final ReservedBoardDAO reservedBoardDAO;

    @Override
    @Transactional
    public void createReservedBoard(BoardDTO reservedBoard) {
        reservedBoardDAO.insertReservedBoard(reservedBoard);
    }

    @Override
    @Transactional
    public BoardDTO getReservedBoardById(long id) {
        return reservedBoardDAO.getReservedBoardById(id);
    }

    @Override
    @Transactional
    public List<BoardDTO> getAllReservedBoards() {
        return reservedBoardDAO.getAllReservedBoards();
    }

    @Override
    @Transactional
    public boolean reservedBoardDelete(long reservedBoardId) {
        try {
            BoardDTO reservedBoard = reservedBoardDAO.getReservedBoardById(reservedBoardId);
            if (reservedBoard != null) {
                reservedBoardDAO.deleteReservedBoard(reservedBoardId);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public void publishReservedPost(Board board) {
        long reservedBoardId = board.getId();

        boardDAO.insertBoard(board);
        reservedBoardDAO.deleteReservedBoard(reservedBoardId);
    }
}
