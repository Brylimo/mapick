package com.enjoytrip.controller;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.enjoytrip.model.dto.CategoryDTO;
import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.service.CategoryService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService cService;
	private final ResponseDto responseDto;
	// 관리자만 사용 가능

	@GetMapping("/categoryCreateForm")
	private String categoryCreateForm() {
		return "category/category_create";
	}
	
	@GetMapping("/categoryUpdateForm")
	private String categoryUpdateForm(@RequestParam Integer categoryId, Model model) {
		if (categoryId == null) {
			model.addAttribute("message", "카테고리 수정 페이지 렌더링에 실패했습니다.");
			return "redirect:/category/list";
        }
		try {
			CategoryDTO categoryDto = cService.getCategoryById(categoryId);
			model.addAttribute("dto", categoryDto);
			return "category/category_update";
		} catch (DataAccessException e) {
			e.printStackTrace();
			return "redirect:/categoryDto/list";
		}
	}

	// 카테고리 가져오기
	@GetMapping
	@ResponseBody
	private ResponseEntity<?> getCategory(@RequestParam Integer categoryId) {
		try {
			CategoryDTO categoryDto = cService.getCategoryById(categoryId);

			return responseDto.success(categoryDto);
		} catch (Exception e) {
			e.printStackTrace();
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 카테고리 생성하기
	@PostMapping
	@ResponseBody
	private ResponseEntity<?> createCategory(@RequestBody CategoryDTO categoryDto) {
		try {
			cService.createCategory(categoryDto);

			return responseDto.success(categoryDto);
		} catch (Exception e) {
			e.printStackTrace();
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 카테고리 리스트 전체 호출
	@GetMapping("/list2")
	@ResponseBody
	private ResponseEntity<?> getCategoryList() {
		try {
			List<CategoryDTO> list = cService.getAllCategories();

			return responseDto.success(list);
		} catch (Exception e) {
			e.printStackTrace();
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 카테고리 업데이트
	@PutMapping
	@ResponseBody
	private ResponseEntity<?> updateCategory(@RequestBody CategoryDTO categoryDto) {
		try {
			cService.categoryUpdate(categoryDto);

			return responseDto.success(categoryDto);
		} catch (Exception e) {
			e.printStackTrace();
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 카테고리 삭제
	@DeleteMapping("{id}")
	@ResponseBody
	private ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
		try {
			cService.categoryDelete(id);

			return responseDto.success();
		} catch (Exception e) {
			e.printStackTrace();
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 카테고리 생성
	@PostMapping("/categoryCreate")
	private String categoryCreate(@RequestParam String categoryName, HttpSession session, Model model) {
		try {
			CategoryDTO categoryDto = new CategoryDTO();
			categoryDto.setCategoryName(categoryName);
			cService.createCategory(categoryDto);
			categoryDto = cService.getCategoryById(categoryDto.getCategoryId());
			
			model.addAttribute("dto", categoryDto);
			model.addAttribute("message", "공지사항 등록 성공");

			return "category/category_detail";
			
		} catch (DataAccessException e) {
			e.printStackTrace();
			model.addAttribute("message", "게시판 등록에 실패했습니다.");
			return "redirect:/category/list";
		}
	}
	
	// 카테고리 디테일
	@GetMapping("/categoryDetail")
	private String categoryDetail(@RequestParam Integer categoryId, Model model, @ModelAttribute CategoryDTO categoryDto) {
        if (categoryId == null) {
        	model.addAttribute("message", "category 상세 조회에 실패했습니다.");
        	return "redirect:/";
        }
		try {
			categoryDto = cService.getCategoryById(categoryId);
			model.addAttribute("dto", categoryDto);
			if (categoryDto != null) {
				model.addAttribute("message", "조회 성공");
			} else {
				model.addAttribute("message", "조회 실패");
			}
			return "category/category_detail";
		} catch (DataAccessException e) {
			e.printStackTrace();
			model.addAttribute("message", "category 상세 조회에 실패했습니다.");
			return "redirect:/category/category_list";
		}
	}
	
	// 카테고리 리스트
	@GetMapping("/list")
	private String categoryList(Model model) {
		try {
			List<CategoryDTO> list = cService.getAllCategories();
			model.addAttribute("list", list);
			if (list != null) {
				model.addAttribute("message", "조회 성공");
			} else {
				model.addAttribute("message", "조회 실패");
			}
			return "category/category_list";
		} catch (DataAccessException e) {
			e.printStackTrace();
			model.addAttribute("message", "조회 실패");
			return "redirect:/";
		}
	}
	
	
	// 카테고리 업데이트
	@PostMapping("/categoryUpdate")
	private String boardUpdate(@RequestParam Integer categoryId, @RequestParam String categoryName, Model model) {
		if (categoryId == null) {
			model.addAttribute("message", "카테고리 수정 실패했습니다.");
			return "redirect:/category/list";
        }
		try {
			CategoryDTO categoryDto = new CategoryDTO();
			categoryDto.setCategoryName(categoryName);
			categoryDto.setCategoryId(categoryId);
			cService.categoryUpdate(categoryDto);
			model.addAttribute("dto", categoryDto);
			return "redirect:/category/list";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "카테고리 수정 실패했습니다.");
			return "redirect:/category/list";
		}
	}
	
	
	// 카테고리 삭제
	@GetMapping("/categoryDelete")
	private String categoryDelete(@RequestParam Integer categoryId, Model model) {
        if (categoryId == null ) {
        	model.addAttribute("message", "카테고리 삭제 실패했습니다.");
			return "redirect:/category/list";
        }
		try {
		 	boolean isDeleted = cService.categoryDelete(categoryId);
		 	if (isDeleted) {
		 		model.addAttribute("message", "삭제 성공");
		 	} else {
		 		model.addAttribute("message", "삭제 실패");
		 	}
		 	return "redirect:/category/list";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "카테고리 삭제 실패했습니다.");
			return "redirect:/category/list";
		}
	}
	

}
