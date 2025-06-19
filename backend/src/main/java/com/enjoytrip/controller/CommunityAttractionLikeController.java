package com.enjoytrip.controller;

import com.enjoytrip.domain.community.CommunityAttractionLike;
import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.dto.community.CommunityAttractionLikeRequestDto;
import com.enjoytrip.dto.community.CommunityLikeResponseDto;
import com.enjoytrip.service.communityAttractionLike.CommunityAttractionLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/community-attraction-likes")
@Slf4j
@RequiredArgsConstructor
public class CommunityAttractionLikeController {
    private final CommunityAttractionLikeService communityAttractionLikeService;
    private final ResponseDto responseDTO;

    @PostMapping
    @ResponseBody
    private ResponseEntity<?> createCommunityAttractionLike(@RequestBody CommunityAttractionLikeRequestDto.CommunityAttractionLikePackage communityAttractionLikePackage, @AuthenticationPrincipal Member member) {
        try {
            CommunityAttractionLike communityAttractionLike = CommunityAttractionLike.builder()
                    .communityAttractionId(communityAttractionLikePackage.getCommunityAttractionId())
                    .memberId(member.getMno())
                    .build();

            boolean isChecked = communityAttractionLikeService.checkCommunityAttractionLike(communityAttractionLike);

            if (!isChecked) {
                communityAttractionLikeService.createCommunityAttractionLike(communityAttractionLike);
            }

            return responseDTO.success(communityAttractionLike);
        } catch (DataAccessException e) {
            return responseDTO.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/check")
    @ResponseBody
    private ResponseEntity<?> checkCommunityAttractionLike(@RequestParam Integer communityAttractionId, @AuthenticationPrincipal Member member) {
        try {
            CommunityAttractionLike communityAttractionLike = CommunityAttractionLike.builder()
                    .communityAttractionId(communityAttractionId)
                    .memberId(member.getMno())
                    .build();

            boolean isChecked = communityAttractionLikeService.checkCommunityAttractionLike(communityAttractionLike);

            return responseDTO.success(CommunityLikeResponseDto.CheckCommunityLike.builder()
                    .checked(isChecked)
                    .build());

        } catch (Exception e) {
            return responseDTO.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{communityAttractionId}")
    @ResponseBody
    private ResponseEntity<?> deleteCommunityAttractionLike(@PathVariable Integer communityAttractionId, @AuthenticationPrincipal Member member) {
        try {
            CommunityAttractionLike communityAttractionLike = CommunityAttractionLike.builder()
                    .communityAttractionId(communityAttractionId)
                    .memberId(member.getMno())
                    .build();

            boolean isChecked = communityAttractionLikeService.checkCommunityAttractionLike(communityAttractionLike);

            if (isChecked) {
                communityAttractionLikeService.deleteCommunityAttractionLike(communityAttractionLike);
            }

            return responseDTO.success(communityAttractionLike);
        } catch (DataAccessException e) {
            return responseDTO.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}