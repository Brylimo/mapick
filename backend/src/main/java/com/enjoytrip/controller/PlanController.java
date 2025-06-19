package com.enjoytrip.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.dto.plan.PlanResponseDto.PlanInfo;
import com.enjoytrip.dto.route.RoutePointDto;
import com.enjoytrip.model.dto.PlanAttractionDTO;
import com.enjoytrip.model.dto.PlanDTO;
import com.enjoytrip.service.PlanAttractionService;
import com.enjoytrip.service.PlanService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;
    private final PlanAttractionService planAttractionService;
    private final ResponseDto responseDto;

    @GetMapping("/list")
    @ResponseBody
    private ResponseEntity<?> planList(@AuthenticationPrincipal Member member) {
        try {
            int userId = member.getMno();
            List<PlanDTO> list = planService.getAllPlans(userId);
            System.out.println(list);
            return responseDto.success(
                    list.stream()
                        .map(dto -> PlanInfo.builder()
                        		.pid(dto.getPid())
                        		.name(dto.getName())
                        		.memberName(dto.getMember().getName())
                        		.createdDate(dto.getCreatedDate())
                        		.updatedDate(dto.getUpdatedDate())
                        		.build())
                        .toList());
        } catch (DataAccessException e) {
            e.printStackTrace();
            log.debug("getPlanList error occurred!");
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    @ResponseBody
    private ResponseEntity<?> planDetail(@RequestParam String name, @AuthenticationPrincipal Member member) {
    	try {
            int mno = member.getMno();
            PlanDTO plan = planService.getPlanByName(name, mno);

            if (plan == null) {
                return responseDto.fail("여행 계획이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
            }

            PlanInfo planInfo = new PlanInfo(plan.getPid(), plan.getName(), plan.getMno(), plan.getMember().getName(), plan.getCreatedDate(), plan.getUpdatedDate());
            return responseDto.success(planInfo);
    	}catch (DataAccessException e) {
            e.printStackTrace();
            log.debug("getPlan error occurred!");
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/timeline")
    private ResponseEntity<?> timeline(@RequestParam String name, @AuthenticationPrincipal Member member) {
    	try {
	        int mno = member.getMno();
	        PlanDTO plan = planService.getPlanByName(name, mno);
	        List<PlanAttractionDTO> planAttractionList = planAttractionService.getAllPlanAttractions(plan.getPid());
	        planAttractionList.sort(
	                Comparator.comparing(
	                    PlanAttractionDTO::getTime,
	                    Comparator.nullsLast(Comparator.naturalOrder())
	                ));
	        return responseDto.success(planAttractionList);
    	}catch(Exception e) {
            e.printStackTrace();
            log.debug("getPlan error occurred!");
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    
    @PostMapping()
    @ResponseBody
    private ResponseEntity<?> planCreate(@RequestBody PlanDTO planDto, @AuthenticationPrincipal Member member) {
        try {
            int userId = member.getMno();
            PlanDTO plan = planService.getPlanByName(planDto.getName(), userId);
            
            if (plan == null) { // plan이 존재하지 않음
                plan = new PlanDTO();
                plan.setName(planDto.getName());
                plan.setMno(userId);
                planService.createPlan(plan);
                plan.setMember(member);
            }
            PlanInfo planInfo = new PlanInfo(plan.getPid(), plan.getName(), userId, member.getName(), plan.getCreatedDate(), plan.getUpdatedDate());

            return responseDto.success(planInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/route")
    public ResponseEntity<?> getRoutePoints(@RequestParam String name, @AuthenticationPrincipal Member member) {
    	try {
    		PlanDTO plan = planService.getPlanByName(name, member.getMno());
    		List<PlanAttractionDTO> attractions = planAttractionService.getAllPlanAttractions(plan.getPid());
    		List<RoutePointDto> route = attractions.stream()
    				.filter(dto -> dto.getTime() != null)
    				.sorted(Comparator.comparing(PlanAttractionDTO::getTime))
    				.map(dto -> {
    					RoutePointDto point = new RoutePointDto(dto.getAttraction().getLatitude(), dto.getAttraction().getLongitude(), dto.getAttraction().getTitle());
    					return point;
    				})
    				.toList();
    		return responseDto.success(route);
    	}catch(Exception e) {
    		 e.printStackTrace();
             return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    
    }

}
