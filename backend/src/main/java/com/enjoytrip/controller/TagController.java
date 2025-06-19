package com.enjoytrip.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.model.dto.TagDTO;
import com.enjoytrip.service.TagService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/tags")
@Slf4j
@RequiredArgsConstructor
public class TagController {
	private final TagService tagSerivce;
	private final ResponseDto responseDto;
	
	@GetMapping("/list/{communityId}")
	@ResponseBody
	private ResponseEntity<?> tagList(@PathVariable("communityId") Integer communityId) {
		try{
			List<TagDTO> list = tagSerivce.findByCommunityId(communityId);

			return responseDto.success(list);
		}catch(Exception e) {
			log.debug("getAllTags error occurred!");
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
