package com.enjoytrip.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.time.Duration;

import com.enjoytrip.domain.community.Board;
import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.board.BoardRequestDto;
import com.enjoytrip.dto.board.BoardResponseDto;
import com.enjoytrip.dto.notice.NoticeRequestDto;
import com.enjoytrip.dto.notice.NoticeResponseDto;
import com.enjoytrip.model.dto.*;
import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.security.dto.CustomUserDetails;
import com.enjoytrip.service.LikeService;
import com.enjoytrip.service.ReservedBoardService;
import com.enjoytrip.util.SimpleDelayQueue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.enjoytrip.model.dto.BoardResDTO;
import com.enjoytrip.model.dto.LikeDTO;
import com.enjoytrip.security.dto.CustomUserDetails;
import com.enjoytrip.service.BoardService;
import com.enjoytrip.service.CommentService;
import com.enjoytrip.service.LikeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
	private final BoardService boardService;
	private final LikeService likeService;
	private final ResponseDto responseDto;
	private final ResponseDto responseDTO;
	private final ReservedBoardService reservedBoardService;
	private final SimpleDelayQueue simpleDelayQueue;

    @GetMapping("/{boardId}")
	@ResponseBody
	private ResponseEntity<?> getBoard(@PathVariable("boardId") Long id, @AuthenticationPrincipal Member member) {
		try {
			BoardResponseDto.BoardInfo boardInfo = boardService.getBoardById(id, member.getMno());

			return responseDTO.success(boardInfo);
		} catch (Exception e) {
			log.debug("getBoard error occurred!");
			return responseDTO.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/{communityId}")
	@ResponseBody
	private ResponseEntity<?> boardCreate(@PathVariable("communityId") int communityId, @RequestBody BoardRequestDto.BoardCreate boardCreate, @AuthenticationPrincipal Member member) {
		try {
			BoardDTO board = BoardDTO.builder()
					.communityId(communityId)
					.title(boardCreate.getTitle())
					.content(boardCreate.getContent())
					.author(member.getMno())
					.build();
			
			// 예약 
			LocalDateTime dateTime = null;
			System.out.println("입력된 값: "+ boardCreate.getReservationDate());
			if (boardCreate.getReservationDate() != null && boardCreate.getReservationTime() != null && !boardCreate.getReservationDate().equals("") && ! boardCreate.getReservationTime().equals("")) {
				LocalDate date = boardCreate.getReservationDate();
				LocalTime time = boardCreate.getReservationTime();
				dateTime = LocalDateTime.of(date, time);
			}
			if (dateTime != null && dateTime.isAfter(LocalDateTime.now())) {
				board.setReservedTime(dateTime);

				reservedBoardService.createReservedBoard(board);

				Board taskBoard = Board.builder()
						.id(board.getId())
						.title(boardCreate.getTitle())
						.createdAt(board.getCreatedAt())
						.author(board.getAuthor())
						.communityId(board.getCommunityId())
						.content(board.getContent())
						.member(board.getMember())
						.updatedAt(board.getUpdatedAt())
						.build();

				simpleDelayQueue.add(new SimpleDelayQueue.DelayedTask(Duration.between(LocalDateTime.now(), dateTime).toMillis(), () -> {
					try {
						reservedBoardService.publishReservedPost(taskBoard);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}));
				return responseDto.success(board);
			}else {
				Board taskBoard = Board.builder()
						.id(board.getId())
						.title(board.getTitle())
						.createdAt(board.getCreatedAt())
						.author(board.getAuthor())
						.communityId(board.getCommunityId())
						.content(board.getContent())
						.member(board.getMember())
						.updatedAt(board.getUpdatedAt())
						.build();

				boardService.createBoard(taskBoard);

				return responseDto.success(BoardResponseDto.BoardInfo.builder()
								.id(board.getId())
								.title(board.getTitle())
								.content(board.getContent())
								.createdAt(board.getCreatedAt())
								.updatedAt(board.getUpdatedAt())
								.build());
			}
			

		} catch (DataAccessException e) {
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	@ResponseBody
	private ResponseEntity<?> boardUpdate(@RequestBody BoardRequestDto.BoardUpdate boardUpdate, @PathVariable Long id, @AuthenticationPrincipal Member member ) {
		try {
			Board board = boardService.boardUpdate(boardUpdate);

			return responseDto.success(BoardResponseDto.BoardInfo.builder()
							.title(board.getTitle())
							.content(board.getContent())
							.build());

		} catch (Exception e) {
			e.printStackTrace();
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{boardId}")
	private ResponseEntity<?> boardDelete(@PathVariable("boardId") Long id) {
		try {
			boolean isDeleted = boardService.boardDelete(id);
			if (isDeleted) {
				return responseDto.success(HttpStatus.OK);
			} else {
				return responseDto.success(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/list")
	@ResponseBody
	private ResponseEntity<?> boardSearch(@RequestParam Integer communityId, @RequestParam String opt, @RequestParam String query, @AuthenticationPrincipal Member member) {
		try {
			List<BoardResponseDto.BoardInfo> list = boardService.getBoardsByOpt(opt, query, communityId, member);

			return responseDto.success(list);
		} catch (Exception e) {
			log.debug("getGugunsData error occurred!");
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/*@PostMapping("/boardLike/{communityId}")
	private String likeBoard(@PathVariable("communityId") int communityId, @RequestParam int id, @ModelAttribute LikeDTO likeDto, Model model , @AuthenticationPrincipal Member member){
		try {
			likeDto.setMemberId(member.getMno());
			likeDto.setBoardId(id);
			boolean alreadyLiked = likeService.checkLiked(id, member.getMno());
			if(alreadyLiked) {
				likeService.deleteLike(likeDto);
			}else {
				likeService.insertLike(likeDto);
			}
			return "redirect:/boards/boardDetail/" + communityId + "?id=" + id;
		} catch (DataAccessException e) {
			e.printStackTrace();
			model.addAttribute("message", "좋아요 생성에 실패했습니다.");
			return "redirect:/boards/list/" + communityId;
		}
	}*/

	/*@PostMapping("/{boardId}/like")
	private String likeBoard(@PathVariable("boardId") int boardId, @AuthenticationPrincipal Member member){
		try {

			boolean alreadyLiked = likeService.checkLiked(id, member.getMno());
			if(alreadyLiked) {
				likeService.deleteLike(likeDto);
			}else {
				likeService.insertLike(likeDto);
			}
			return "redirect:/boards/boardDetail/" + communityId + "?id=" + id;
		} catch (DataAccessException e) {
			e.printStackTrace();
			model.addAttribute("message", "좋아요 생성에 실패했습니다.");
			return "redirect:/boards/list/" + communityId;
		}
	}*/

}
