package com.enjoytrip.controller;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.dto.notice.NoticeRequestDto.NoticeRegist;
import com.enjoytrip.dto.notice.NoticeResponseDto.NoticeInfo;
import com.enjoytrip.model.dto.NoticeDTO;
import com.enjoytrip.service.NoticeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;
    private final ResponseDto responseDto;
    
    @GetMapping("/list")
    @ResponseBody
    private ResponseEntity<?> noticeList() {
        try {
            List<NoticeDTO> list = noticeService.getAllNotices();
            System.out.println(list);
            List<NoticeInfo> result = list.stream()
                    .map(n -> NoticeInfo.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .content(n.getContent())
                        .authorId(n.getAuthor())    
                        .authorName(n.getMember().getName()) 
                        .createdAt(n.getCreatedAt())
                        .updatedAt(n.getUpdatedAt())
                        .build())
                    .toList();
            return responseDto.success(result);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    private ResponseEntity<?> noticeDetail(@PathVariable Long id) {
        try {
            NoticeDTO notice = noticeService.getNoticeById(id);
            return responseDto.success(NoticeInfo.builder()
            		.id(notice.getId())
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .authorId(notice.getAuthor())    
                    .authorName(notice.getMember().getName()) 
                    .createdAt(notice.getCreatedAt())
                    .updatedAt(notice.getUpdatedAt())
                    .build());
        } catch (DataAccessException e) {
        	 e.printStackTrace();
             return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping()
    @ResponseBody
    private ResponseEntity<?> noticeCreate(@RequestBody NoticeRegist noticeReq, @AuthenticationPrincipal Member member) {
        try {
            int userId = member.getMno();
            NoticeDTO notice = new NoticeDTO();
            notice.setAuthor(userId);
            notice.setContent(noticeReq.getContent());
            notice.setTitle(noticeReq.getTitle());
            noticeService.createNotice(notice);
            notice = noticeService.getNoticeById(notice.getId());
            return responseDto.success(NoticeInfo.builder()
            		.id(notice.getId())
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .authorId(notice.getAuthor())    
                    .authorName(notice.getMember().getName()) 
                    .createdAt(notice.getCreatedAt())
                    .updatedAt(notice.getUpdatedAt())
                    .build());
        } catch (DataAccessException e) {
        	e.printStackTrace();
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @PutMapping("/{id}") 
    @ResponseBody
    private ResponseEntity<?> noticeUpdate(@RequestBody NoticeRegist noticeReq, @PathVariable Long id, @AuthenticationPrincipal Member member ) {
    	try {
    		System.out.println(noticeReq.getTitle());
    		NoticeDTO notice = new NoticeDTO();
    		notice.setId(id);
    		notice.setContent(noticeReq.getContent());
    		notice.setTitle(noticeReq.getTitle());
            noticeService.noticeUpdate(notice);
            notice = noticeService.getNoticeById(id);
            return responseDto.success(NoticeInfo.builder()
            		.id(notice.getId())
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .authorId(notice.getAuthor())    
                    .authorName(notice.getMember().getName()) 
                    .createdAt(notice.getCreatedAt())
                    .updatedAt(notice.getUpdatedAt())
                    .build());
        } catch (Exception e) {
        	e.printStackTrace();
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @DeleteMapping("/{id}")
    private ResponseEntity<?> noticeDelete(@PathVariable Long id) {
    	try {
            boolean isDeleted = noticeService.noticeDelete(id);
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
}
