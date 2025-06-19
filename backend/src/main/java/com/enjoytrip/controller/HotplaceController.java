package com.enjoytrip.controller;

import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.model.dto.*;
import com.enjoytrip.security.dto.CustomUserDetails;
import com.enjoytrip.service.AttractionService;
import com.enjoytrip.service.HotplaceService;
import com.enjoytrip.util.TourUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/hotplaces")
@RequiredArgsConstructor
public class HotplaceController {
    private final HotplaceService hotplaceService;
    private final AttractionService attractionService;

    private final TourUtil tourUtil;
    private final ResponseDto responseDto;

    @GetMapping("/list")
    @ResponseBody
    private ResponseEntity<?> list(@AuthenticationPrincipal Member member) {
        try {
            List<HotplaceDTO> list = hotplaceService.getHotplace(member.getMno());
            return responseDto.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 핫플레이스 ID로 상세 정보 조회
    @GetMapping("/{hpid}")
    public ResponseEntity<?> getHotplaceById(@PathVariable Long hpid) {
        try {
            HotplaceDTO hotplace = hotplaceService.getHotplaceById(hpid);
            return responseDto.success(hotplace);
        } catch (Exception e) {
            e.printStackTrace();
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping()
    @ResponseBody
    private ResponseEntity<?> create(@AuthenticationPrincipal Member member, @RequestBody AttractionDTO dto) {
        try {
            String decodedTitle = URLDecoder.decode(dto.getTitle(), StandardCharsets.UTF_8);

            // attraction 정보 불러오기
            AttractionDataDTO attractionDataDTO = tourUtil.searchAttractionsByTitle(decodedTitle);
            attractionService.createAttraction(attractionDataDTO);

            HotplaceDTO hotplaceDto = new HotplaceDTO();
            hotplaceDto.setMno(member.getMno());

            if (attractionDataDTO != null) {
                hotplaceDto.setAttractionId(attractionDataDTO.getNo());
            }

            hotplaceService.addHotplace(hotplaceDto);

            return responseDto.success(hotplaceDto);
        } catch (Exception e) {
            e.printStackTrace();
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{hotplaceId}")
    @ResponseBody
    private ResponseEntity<?> delete(@AuthenticationPrincipal Member member, @PathVariable int hotplaceId) {
        try {
            hotplaceService.deleteHotplace(member.getMno(), hotplaceId);

            return responseDto.success();
        } catch (Exception e) {
            e.printStackTrace();
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

/*    @DeleteMapping("/{hotplaceId}")
    public ResponseEntity<?> deleteHotplace(@AuthenticationPrincipal Member member, @PathVariable int hotplaceId) {
        hotplaceService.deleteHotplace(member.getMno(), hotplaceId);

        return responseDto.success();
    }*/
}