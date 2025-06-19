package com.enjoytrip.controller;

import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.dto.community.CommunityAttractionResponseDto;
import com.enjoytrip.model.dto.*;
import com.enjoytrip.service.*;
import com.enjoytrip.service.communityAttractionTag.CommunityAttractionTagService;
import com.enjoytrip.util.S3UploaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/community-attractions")
@Slf4j
@RequiredArgsConstructor
public class CommunityAttractionController {
    private final AttractionService attractionService;
    private final TagService tagService;
    private final CommunityAttractionService communityAttractionService;
    private final CommunityTagService communityTagService;
    private final CommunityAttractionTagService communityAttractionTagService;

    private final S3UploaderUtil s3UploaderUtil;
    private final ResponseDto responseDto;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    private ResponseEntity<?> communityAttractionCreate(@ModelAttribute AttractionDataDTO attractionDataDTO,
                                                        @RequestParam(required = false) List<String> hashtagList,
                                                        @RequestParam Integer communityid,
                                                        @RequestPart(value = "imgFile", required = false) MultipartFile file) {
        try {
            // 해당 attraction이 존재하나 조회
            AttractionDTO attractionDTO = attractionService.getAttraction(attractionDataDTO.getTitle(), attractionDataDTO.getMapy(), attractionDataDTO.getMapx());

            int aId = -1;
            if (attractionDTO == null) { // 없을 경우
                if (file != null) {
                    FileDTO fileDTO = s3UploaderUtil.uploadFile(file, "post");
                    attractionDataDTO.setFirstimage(fileDTO.getPath()); // 파일을 따로 저장하지 않고 attractionData에 이미지 path를 저장
                }

                attractionService.createAttractionCustom(attractionDataDTO);
                aId = attractionDataDTO.getNo();
            } else { // 존재할 경우
                aId = attractionDTO.getNo();
            }

            if (aId == -1) throw new Exception();

            CommunityAttractionDTO dto = new CommunityAttractionDTO();
            dto.setAttractionId(aId);
            dto.setCommunityId(communityid);

            communityAttractionService.createCommunityAttraction(dto);

            // tag 저장
            if (hashtagList != null) {
                for (String tagName : hashtagList) {
                    TagDTO tag = tagService.findByName(tagName);

                    if (tag == null) { // 해당 이름의 태그가 존재하지 않음
                        tag = tagService.createTag(tagName);
                    }

                    // communityTag 저장
                    CommunityTagDTO communityTagDTO = new CommunityTagDTO();
                    communityTagDTO.setCommunityId(communityid);
                    communityTagDTO.setTagId(tag.getTagId());

                    communityTagService.createCommunityTag(communityTagDTO);

                    // communityAttractionTag 저장
                    CommunityAttractionTagDTO communityAttractionTagDTO = new CommunityAttractionTagDTO();
                    communityAttractionTagDTO.setCommunityAttractionId(dto.getCommunityAttractionId());
                    communityAttractionTagDTO.setTagId(tag.getTagId());

                    communityAttractionTagService.createCommunityAttractionTag(communityAttractionTagDTO);
                }
            }

            return responseDto.success(dto);
        } catch (Exception e) {
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    @ResponseBody
    private ResponseEntity<?> getCommunityAttractionData(
            @RequestParam(required = false) String query,
            @RequestParam Integer communityid,
            @RequestParam(required = false) List<String> tags,
            @AuthenticationPrincipal Member member) {
        try {
            List<CommunityAttractionDTO> communityAttractions = communityAttractionService.findAllCommunityAttractions(communityid, query, tags, member);

            return responseDto.success(communityAttractions);
        } catch (Exception e) {
            log.debug("getCommunityAttractionData error occurred!");
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/popular")
    @ResponseBody
    private ResponseEntity<?> getPopularCommunityAttractions() {
        try {
            List<PopularAttractionDTO> list = communityAttractionService.getPopularAttractions();

            return responseDto.success(list);
        } catch (Exception e) {
            log.debug("getPopularCommunityAttractions error occurred!");
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
