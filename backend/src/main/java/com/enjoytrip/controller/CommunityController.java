package com.enjoytrip.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.enjoytrip.domain.member.Member;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enjoytrip.model.dto.CategoryDTO;
import com.enjoytrip.model.dto.CommunityDTO;
import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.model.dto.TagDTO;
import com.enjoytrip.service.CategoryService;
import com.enjoytrip.service.CommunityService;
import com.enjoytrip.service.TagService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/communities")
@Slf4j
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final ResponseDto responseDto;
    
    @GetMapping("/communityCreateForm")
	private String communityCreateForm() {
		return "community/community_create";
	}

    @GetMapping("/communityUpdateForm")
   	private String communityUpdateForm(@RequestParam int communityId, Model model, @AuthenticationPrincipal Member member) {
    	CommunityDTO communityDto = communityService.getCommunityById(communityId);
    	communityDto.setCategoryList(categoryService.findByCommunityId(communityId));
    	communityDto.setTagList(tagService.findByCommunityId(communityId));
    	model.addAttribute("dto", communityDto);
   		return "community/community_update";
   	}

	@GetMapping("/list")
	@ResponseBody
	private ResponseEntity<?> list() {
		try {
			List<CommunityDTO> list = communityService.getAllCommunities();

			return responseDto.success(list);
		} catch (Exception e) {
			e.printStackTrace();
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

    @GetMapping("/communityList")
    private String communityList(Model model) {
    	List<CommunityDTO> list = communityService.getAllCommunities();
    	model.addAttribute("list", list);
        return "community/community_list";
    }

    @GetMapping("/communityMain/{name}")
    private String communityMain(@PathVariable("name") String name, Model model, @AuthenticationPrincipal Member member) {
		model.addAttribute("communityId", Integer.parseInt(name));
        model.addAttribute("user", member);
        return "community/community_main";
    }

	// 커뮤니티 생성
	@PostMapping("/communityCreate")
	@ResponseBody
	private ResponseEntity<?> communityCreate(@RequestBody CommunityDTO communityDto, Model model, @AuthenticationPrincipal Member member) {
		try {
			int userId = member.getMno();
			communityDto.setRegisteredBy(userId);
			communityService.createCommunity(communityDto);
			return responseDto.success("커뮤니티 생성 성공");
		}catch(Exception e) {
			e.printStackTrace();
			return responseDto.fail("커뮤니티 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// 커뮤니티 수정
	@PostMapping("/communityUpdate")
	private String communityUpdate(@ModelAttribute CommunityDTO communityDto, @RequestParam("tagList[]") List<String> tagNames,
		    @RequestParam("categoryList[]") List<String> categoryNames, Model model, @AuthenticationPrincipal Member member) {
		try {
			if(member.getMno() != communityDto.getRegisteredBy() && !member.getRole().equals("ADMIN")) {
				model.addAttribute("message", "게시글 수정 권한이 없습니다");
				return "redirect:/communities/communityList";
			}
		    List<CategoryDTO> categories = categoryNames.stream()
		        .map(name -> {
		            CategoryDTO dto = new CategoryDTO();
		            dto.setCategoryName(name);
		            return dto;
		        }).collect(Collectors.toList());

		    List<TagDTO> tags = tagNames.stream()
		        .map(name -> {
		            TagDTO dto = new TagDTO();
		            dto.setName(name);
		            return dto;
		        }).collect(Collectors.toList());
		    communityDto.setCategoryList(categories);
		    communityDto.setTagList(tags);
			communityService.updateCommunity(communityDto);
			communityDto = communityService.getCommunityById(communityDto.getCommunityId());
			model.addAttribute("communityId", communityDto.getCommunityId());
			return "community/communityDetail";
		}catch(Exception e) {
			e.printStackTrace();
			return "redirect:/communities/communityList";
		}
	}

	// 커뮤니티 수정 rest api
	@PutMapping
	@ResponseBody
	private ResponseEntity<?> updateCommunity(@RequestBody CommunityDTO communityDto, @AuthenticationPrincipal Member member) {
		try {
			if(member.getMno() != communityDto.getRegisteredBy() && !member.getRole().equals("ADMIN")) {
				return responseDto.fail("게시글 수정 권한이 없습니다", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			List<CategoryDTO> categories = communityDto.getCategoryList().stream()
					.map(category -> {
						CategoryDTO dto = new CategoryDTO();
						dto.setCategoryName(category.getCategoryName());
						return dto;
					}).collect(Collectors.toList());

			List<TagDTO> tags = communityDto.getTagList().stream()
					.map(tag -> {
						TagDTO dto = new TagDTO();
						dto.setName(tag.getName());
						return dto;
					}).collect(Collectors.toList());

			communityDto.setCategoryList(categories);
			communityDto.setTagList(tags);
			communityService.updateCommunity(communityDto);
			communityDto = communityService.getCommunityById(communityDto.getCommunityId());

			return responseDto.success(communityDto);
		} catch(Exception e) {
			e.printStackTrace();
			return responseDto.fail("검색 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 커뮤니티 검색 - 커뮤니티 제목 가져오기
	@GetMapping("/searchCommunity")
	@ResponseBody
	private ResponseEntity<?> getCommunities(@RequestParam String opt, @RequestParam String query, Model model){
		try {
			List<CommunityDTO> list = new ArrayList<>();
			switch(opt) {
			case "name":
				list = new ArrayList<>();
				CommunityDTO res = communityService.getCommunitiesByCommunityName(query); // 커뮤니티 이름
				list.add(res);
				break;
			case "category":
				list = communityService.getCommunitiesByCategory(query); // 카테고리
				break;
			case "tag":
				list = communityService.getCommunitiesByTag(query); // 태그
				
			}
			if(list==null) {
				model.addAttribute("message", "해당 커뮤니티 없음");
				return responseDto.success(list);
			}
			model.addAttribute("list", list);
			return responseDto.success(list);
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "커뮤니티 리스트 접근 실패");
			return responseDto.fail("검색 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 커뮤니티 디테일
	@GetMapping("/communityDetail")
    private String communityDetail(@RequestParam int communityId, Model model) {
        try {
            CommunityDTO community = communityService.getCommunityById(communityId);
            List<CategoryDTO> categories = categoryService.findByCommunityId(communityId);
            List<TagDTO> tags = tagService.findByCommunityId(communityId);
            community.setCategoryList(categories);
            community.setTagList(tags);
            model.addAttribute("dto", community);
            return "community/community_detail";
        } catch (DataAccessException e) {
            e.printStackTrace();
            model.addAttribute("message", "커뮤니티 상세 조회에 실패했습니다.");
            return "redirect:/communities/communityList";
        }
    }

	@GetMapping("/communityDelete")
	private String communityDelete(@RequestParam int communityId, Model model) {
		try {
			communityService.communityDelete(communityId);
			return "redirect:/communities/communityList";
		}catch (DataAccessException e) {
            e.printStackTrace();
            model.addAttribute("message", "커뮤니티 삭제에 실패했습니다.");
            return "redirect:/communities/communityList";
        }
    }

	@GetMapping
	@ResponseBody
	private ResponseEntity<?> getCommunity(@RequestParam int communityId) {
		try {
			CommunityDTO community = communityService.getCommunityById(communityId);

			List<CategoryDTO> categories = categoryService.findByCommunityId(communityId);
			List<TagDTO> tags = tagService.findByCommunityId(communityId);

			community.setCategoryList(categories);
			community.setTagList(tags);

			return responseDto.success(community);
		} catch (Exception e) {
			e.printStackTrace();
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("{id}")
	@ResponseBody
	private ResponseEntity<?> communityDelete(@PathVariable Integer id) {
		try {
			communityService.communityDelete(id);

			return responseDto.success();
		} catch (Exception e) {
			e.printStackTrace();
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/top3")
	@ResponseBody
	public ResponseEntity<?> getTop3PopularCommunities() {
		try {
			List<CommunityDTO> list = communityService.getTop3PopularCommunities();
			return responseDto.success(list);
		} catch (Exception e) {
			e.printStackTrace();
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
