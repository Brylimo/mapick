package com.enjoytrip.controller;

import com.enjoytrip.domain.community.CommunityLike;
import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.dto.community.CommunityLikeRequestDto;
import com.enjoytrip.dto.community.CommunityLikeResponseDto;
import com.enjoytrip.service.communityLike.CommunityLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/community-likes")
@Slf4j
@RequiredArgsConstructor
public class CommunityLikeController {
    private final CommunityLikeService communityLikeService;
    private final ResponseDto responseDTO;

    @PostMapping
    @ResponseBody
    private ResponseEntity<?> createCommunityLike(@RequestBody CommunityLikeRequestDto.CommunityLikePackage communityLikePackage, @AuthenticationPrincipal Member member) {
        try {
            CommunityLike communityLike = CommunityLike.builder()
                    .communityId(communityLikePackage.getCommunityId())
                    .memberId(member.getMno())
                    .build();

            boolean isChecked = communityLikeService.checkCommunityLike(communityLike);

            if (!isChecked) {
                communityLikeService.createCommunityLike(communityLike);
            }

            return responseDTO.success(communityLike);
        } catch (DataAccessException e) {
            return responseDTO.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/check")
    @ResponseBody
    private ResponseEntity<?> checkCommunityLike(@RequestParam Integer communityId, @AuthenticationPrincipal Member member) {
        try {
            CommunityLike communityLike = CommunityLike.builder()
                    .communityId(communityId)
                    .memberId(member.getMno())
                    .build();

            boolean isChecked = communityLikeService.checkCommunityLike(communityLike);


            return responseDTO.success(CommunityLikeResponseDto.CheckCommunityLike.builder()
                            .checked(isChecked)
                            .build());

        } catch (Exception e) {
            return responseDTO.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{communityId}")
    @ResponseBody
    private ResponseEntity<?> deleteCommunityLike(@PathVariable Integer communityId, @AuthenticationPrincipal Member member) {
        try {
            CommunityLike communityLike = CommunityLike.builder()
                    .communityId(communityId)
                    .memberId(member.getMno())
                    .build();

            boolean isChecked = communityLikeService.checkCommunityLike(communityLike);

            if (isChecked) {
                communityLikeService.deleteCommunityLike(communityLike);
            }

            return responseDTO.success(communityLike);
        } catch (DataAccessException e) {
            return responseDTO.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}