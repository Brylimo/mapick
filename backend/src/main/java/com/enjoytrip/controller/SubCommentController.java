package com.enjoytrip.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.dto.comment.CommentResponseDto;
import com.enjoytrip.dto.comment.CommentResponseDto.CommentInfo;
import com.enjoytrip.dto.comment.SubCommentResponseDto;
import com.enjoytrip.dto.comment.SubCommentResponseDto.SubCommentInfo;
import com.enjoytrip.model.dto.SubCommentDTO;
import com.enjoytrip.security.dto.CustomUserDetails;
import com.enjoytrip.service.SubCommentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/sub-comment")
@Slf4j
@RequiredArgsConstructor
public class SubCommentController {
	private final SubCommentService subCommentService;
	private final ResponseDto responseDTO;
	
	@PostMapping("/{commentId}")
	@ResponseBody
	private ResponseEntity<?> InsertComment(@PathVariable("commentId") int commentId, @RequestBody SubCommentDTO subCommentDto, @AuthenticationPrincipal Member member) {
		try {
			subCommentDto.setCommentId(commentId);
			subCommentDto.setMemberId(member.getMno());
			subCommentDto.setMember(member);
			subCommentService.insertSubComment(subCommentDto);
			subCommentDto = subCommentService.getSubCommentById(subCommentDto.getSubCommentId());
			SubCommentResponseDto.SubCommentInfo res = SubCommentInfo.builder()
					.commentId(subCommentDto.getCommentId())
					.subCommentId(subCommentDto.getSubCommentId())
					.memberId(subCommentDto.getMemberId())
                    .memberName(subCommentDto.getMember().getName()) 
                    .content(subCommentDto.getContent())
                    .regDate(subCommentDto.getRegDate())
                    .modDate(subCommentDto.getModDate())
                    .build();
	        return responseDTO.success(res);
		}catch(Exception e) {
			e.printStackTrace();
			log.debug("Insert subCommentDto error occurred!");
			return responseDTO.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/list/{commentId}")
	@ResponseBody
	private ResponseEntity<?> getAllSubComments(@PathVariable("commentId") int commentId) {
		try {
			List<SubCommentDTO> subComments = subCommentService.getAllSubCommentsByCommentId(commentId);
			List<SubCommentResponseDto.SubCommentInfo> result = subComments.stream()
				    .map(c -> SubCommentResponseDto.SubCommentInfo.builder()
				        .commentId(c.getCommentId()) 
				        .subCommentId(c.getSubCommentId()) 
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
			log.debug("get all subComments error occurred!");
			return responseDTO.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/{commentId}")
	@ResponseBody
	private ResponseEntity<?> updateSubComment(@PathVariable("commentId") int commentId, @RequestBody SubCommentDTO subCommentDto, @AuthenticationPrincipal Member member) {
		try {
			subCommentDto.setCommentId(commentId);
			subCommentService.updateSubComment(subCommentDto);
			subCommentDto = subCommentService.getSubCommentById(subCommentDto.getSubCommentId());
			SubCommentResponseDto.SubCommentInfo res = SubCommentInfo.builder()
					.subCommentId(subCommentDto.getSubCommentId())
					.commentId(subCommentDto.getCommentId())
					.memberId(subCommentDto.getMemberId())
					.memberName(subCommentDto.getMember().getName())
					.content(subCommentDto.getContent())
					.regDate(subCommentDto.getRegDate())
					.modDate(subCommentDto.getModDate())
					.build();
			return responseDTO.success(res);
		}catch(Exception e) {
			e.printStackTrace();
			log.debug("update SubComment error occurred!");
			return responseDTO.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/{commentId}")
	private ResponseEntity<?> deleteSubComment(@PathVariable Long commentId, @RequestParam int subCommentId) {
    	try {
            boolean isDeleted = subCommentService.deleteSubComment(subCommentId);
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
