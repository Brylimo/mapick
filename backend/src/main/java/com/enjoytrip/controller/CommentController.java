package com.enjoytrip.controller;

import java.util.List;

import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.dto.comment.CommentResponseDto;
import com.enjoytrip.dto.comment.CommentResponseDto.CommentInfo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.enjoytrip.domain.member.Member;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enjoytrip.model.dto.CommentDTO;
import com.enjoytrip.security.dto.CustomUserDetails;
import com.enjoytrip.service.BoardService;
import com.enjoytrip.service.CommentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/comment")
@Slf4j
@RequiredArgsConstructor
public class CommentController {
	private final CommentService commentService;
	private final ResponseDto responseDTO;

	@PostMapping("/{boardId}")
	@ResponseBody
	private ResponseEntity<?> InsertComment(@PathVariable("boardId") long boardId, @RequestBody CommentDTO commentDto, @AuthenticationPrincipal Member member) {
		try {
			commentDto.setMemberId(member.getMno());
			commentDto.setBoardId(boardId);
			commentService.insertComment(commentDto);
			commentDto = commentService.getCommentById(commentDto.getCommentId());
			CommentResponseDto.CommentInfo res = CommentInfo.builder()
					.commentId(commentDto.getCommentId())
					.boardId(commentDto.getBoardId())
					.memberId(commentDto.getMemberId())
                    .memberName(commentDto.getMember().getName()) 
                    .content(commentDto.getContent())
                    .regDate(commentDto.getRegDate())
                    .modDate(commentDto.getModDate())
                    .build();
	        return responseDTO.success(res);
		}catch(Exception e) {
			e.printStackTrace();
			log.debug("InsertComment error occurred!");
			return responseDTO.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/list/{boardId}")
	@ResponseBody
	private ResponseEntity<?> getAllComments(@PathVariable("boardId") long boardId) {
		try {
			List<CommentDTO> comments = commentService.getAllCommentsByBoardId(boardId);
			
			List<CommentResponseDto.CommentInfo> result = comments.stream()
	                .map(c -> CommentResponseDto.CommentInfo.builder()
	                        .commentId(c.getCommentId())
	                        .boardId(c.getBoardId())
	                        .memberId(c.getMemberId())
	                        .memberName(c.getMember().getName()) 
	                        .content(c.getContent())
	                        .regDate(c.getRegDate())
	                        .modDate(c.getModDate())
	                        .build())
	                .toList();

	        return responseDTO.success(result);
		}catch(Exception e) {
			e.printStackTrace();
			log.debug("get all Comments error occurred!");
			return responseDTO.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{boardId}")
	@ResponseBody
	private ResponseEntity<?> updateComment(@PathVariable("boardId") long boardId, @RequestBody CommentDTO commentDto, @AuthenticationPrincipal Member member) {
		try {
			System.out.println("id:" + commentDto.getCommentId());
			commentDto.setBoardId(boardId);
			commentService.updateComment(commentDto);
			commentDto = commentService.getCommentById(commentDto.getCommentId());
			CommentResponseDto.CommentInfo res = CommentInfo.builder()
					.commentId(commentDto.getCommentId())
					.boardId(commentDto.getBoardId())
					.memberId(commentDto.getMemberId())
					.memberName(commentDto.getMember().getName())
					.content(commentDto.getContent())
					.regDate(commentDto.getRegDate())
					.modDate(commentDto.getModDate())
					.build();
			return responseDTO.success(res);
		}catch(Exception e) {
			e.printStackTrace();
			log.debug("update Comment error occurred!");
			return responseDTO.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/{boardId}")
	private ResponseEntity<?> deleteComment(@PathVariable("boardId") Long boardId, @RequestParam Long commentId) {
    	try {
            boolean isDeleted = commentService.deleteComment(commentId);
            if (isDeleted) {
            	return responseDTO.success(HttpStatus.OK);
            } else {
            	return responseDTO.success(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            return responseDTO.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
