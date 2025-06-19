package com.enjoytrip.controller;

import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.dto.community.CommunityMemberRequestDto;
import com.enjoytrip.dto.email.EmailResponseDto;
import com.enjoytrip.model.dto.CommunityDTO;
import com.enjoytrip.model.dto.CommunityJoinRequestDTO;
import com.enjoytrip.service.CommunityService;
import com.enjoytrip.service.communityMember.CommunityMemberService;
import com.enjoytrip.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/community-members")
@Slf4j
@RequiredArgsConstructor
public class CommunityMemberController {
    private final CommunityService communityService;
    private final CommunityMemberService communityMemberService;

    private final EmailUtil emailUtil;
    private final ResponseDto responseDto;

    @PostMapping("/review-request")
    @ResponseBody
    private ResponseEntity<?> requestCommunityMemberReview(@RequestBody CommunityMemberRequestDto.CommunityMemberReview communityMemberReview, @AuthenticationPrincipal Member member) {
        try {
            CommunityDTO community = communityService.getCommunityById(communityMemberReview.getCommunityId());
            CommunityJoinRequestDTO joinRequest = CommunityJoinRequestDTO.builder()
                    .communityId(communityMemberReview.getCommunityId())
                    .memberId(member.getMno())
                    .nickname(communityMemberReview.getNickname())
                    .reason(communityMemberReview.getReason())
                    .build();
            communityMemberService.createJoinRequest(joinRequest);

            // 이메일 보내기
            EmailResponseDto.EmailMessage emailMessage = EmailResponseDto.EmailMessage.builder()
                    .to(community.getMember().getEmail())
                    .subject("[Mapick] 새로운 커뮤니티 가입 요청이 들어왔습니다.")
                    .message(community.getMember().getName())
                    .build();

            communityMemberReview.setName(member.getName());
            emailUtil.sendCommunityRegistMail(emailMessage, communityMemberReview, "email/community_signup");

            return responseDto.success("커뮤니티 관리자에게 이메일을 전송했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return responseDto.fail("검색 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/request/{communityId}")
    public ResponseEntity<?> getJoinRequestsByCommunity(@PathVariable int communityId) {
        try {
            List<CommunityJoinRequestDTO> requests = communityMemberService.getRequestsByCommunityId(communityId);
            System.out.println(requests);
            return responseDto.success(requests);
        } catch (Exception e) {
            return responseDto.fail("가입 요청 조회 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{joinRequestId}/approve")
    @ResponseBody
    public ResponseEntity<?> approveJoinRequest(@PathVariable int joinRequestId) {
        try {
            communityMemberService.approveJoinRequest(joinRequestId);
            return responseDto.success("가입 요청을 수락했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return responseDto.fail("가입 수락 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{joinRequestId}/reject")
    @ResponseBody
    public ResponseEntity<?> rejectJoinRequest(@PathVariable int joinRequestId) {
        try {
            communityMemberService.rejectJoinRequest(joinRequestId);
            return responseDto.success("가입 요청을 거절했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return responseDto.fail("가입 거절 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{communityId}/check-member")
    public ResponseEntity<?> checkMember(@PathVariable int communityId, @AuthenticationPrincipal Member member){
    	try {
    		int memberId = member.getMno();
    	    boolean isMember = communityMemberService.isMember(communityId, memberId);
    	    return responseDto.success(isMember);
    	}catch(Exception e) {
    		e.printStackTrace();
            return responseDto.fail("멤버 확인 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }

}
