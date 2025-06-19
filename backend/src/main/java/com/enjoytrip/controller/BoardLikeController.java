package com.enjoytrip.controller;

import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.model.dto.LikeDTO;
import com.enjoytrip.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/board-likes")
@RequiredArgsConstructor
public class BoardLikeController {
    private final LikeService likeService;
    private final ResponseDto responseDTO;

    @PostMapping("/{boardId}")
    @ResponseBody
    private ResponseEntity<?> likeBoard(@PathVariable("boardId") int boardId, @AuthenticationPrincipal Member member){
        try {
            LikeDTO likeDto = LikeDTO.builder()
                    .memberId(member.getMno())
                    .boardId(boardId)
                    .build();

            boolean alreadyLiked = likeService.checkLiked(boardId, member.getMno());
            if (alreadyLiked) {
                likeService.deleteLike(likeDto);
            } else {
                likeService.insertLike(likeDto);
            }

            int cnt = likeService.getAllLikeCnt(boardId);
            return responseDTO.success(cnt);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return responseDTO.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
