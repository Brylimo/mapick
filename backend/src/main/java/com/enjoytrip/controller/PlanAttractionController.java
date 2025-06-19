package com.enjoytrip.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.dto.planAttraction.PlanAttractionResponseDto.PlanAttractionInfo;
import com.enjoytrip.dto.planAttraction.PlanAttractionUpdateRequestDto.PlanAttractionUdapteInfo;
import com.enjoytrip.model.dto.AttractionDTO;
import com.enjoytrip.model.dto.AttractionDataDTO;
import com.enjoytrip.model.dto.PlanAttractionDTO;
import com.enjoytrip.service.AttractionService;
import com.enjoytrip.service.PlanAttractionService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/plan-attractions")
@RequiredArgsConstructor
public class PlanAttractionController {

    private final AttractionService attractionService;

    private final ResponseDto responseDto;
    private final PlanAttractionService planAttractionService;

    @PostMapping
    @ResponseBody
    private ResponseEntity<?> planAttractionCreate(@RequestBody AttractionDataDTO attractionDataDTO, @RequestParam Integer planid) {
        try {
            AttractionDTO attractionDTO = attractionService.getAttraction(attractionDataDTO.getTitle(), attractionDataDTO.getMapy(), attractionDataDTO.getMapx());

            int aId = -1;
            if (attractionDTO == null) {
                attractionService.createAttraction(attractionDataDTO);
                aId = attractionDataDTO.getNo();
            } else {
                aId = attractionDTO.getNo();
            }

            if (aId == -1) throw new Exception();

            PlanAttractionDTO dto = new PlanAttractionDTO();
            dto.setAttractionid(aId);
            dto.setPlanid(planid);

            PlanAttractionDTO planAttractionDTO = planAttractionService.createPlanAttraction(dto);

            return responseDto.success(planAttractionDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/direct")
    @ResponseBody
    private ResponseEntity<?> planAttractionCreate(@RequestBody AttractionDTO attractionDTO, @RequestParam Integer planid) {
        try {
            AttractionDTO attraction = attractionService.getAttraction(attractionDTO.getTitle(), attractionDTO.getLatitude(), attractionDTO.getLongitude());

            int aId = -1;
            if (attraction == null) {
                return responseDto.fail("여행 계획 저장에 실패했습니다.", HttpStatus.BAD_REQUEST);
            } else {
                aId = attraction.getNo();
            }

            if (aId == -1) throw new Exception();

            PlanAttractionDTO dto = new PlanAttractionDTO();
            dto.setAttractionid(aId);
            dto.setPlanid(planid);

            PlanAttractionDTO planAttractionDTO = planAttractionService.createPlanAttraction(dto);

            return responseDto.success(planAttractionDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/list")
    @ResponseBody
    private ResponseEntity<?> planAttractionList(@RequestParam Integer planid){
    	 try {
             List<PlanAttractionDTO> planAttractionList = planAttractionService.getAllPlanAttractions(planid);
             List<PlanAttractionInfo> result = planAttractionList.stream()
                     .map(dto -> PlanAttractionInfo.builder()
                             .ppid(dto.getPpid())
                             .idx(dto.getIdx())
                             .planid(dto.getPlanid())
                             .attractionid(dto.getAttractionid())
                             .attractionName(dto.getAttraction().getTitle())
                             .exp(dto.getExp())
                             .build())
                     .toList();
             return responseDto.success(result);
         } catch (Exception e) {
        	 e.printStackTrace();
             log.debug("getPlanAttractionList error occurred!");
 			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
         }
    	
    }

    
    @DeleteMapping("/{id}")
    private void planAttractionDelete(@PathVariable("id") Integer paId, HttpServletResponse response) {

        try {
            if (paId == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                boolean success = planAttractionService.planAttractionDelete(paId);

                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    @PutMapping
    @ResponseBody
    private ResponseEntity<?> planAttractionUpdate(@RequestBody PlanAttractionUdapteInfo planAttractionUpdate) {
        try {
        	PlanAttractionDTO planAttraction = planAttractionService.getPlanAttractionById(planAttractionUpdate.getPpid());
        	if (planAttraction == null) {
                return responseDto.fail("Not found", HttpStatus.NOT_FOUND);
            }
        	planAttraction.setExp(planAttractionUpdate.getExp());
        	planAttraction.setTime(planAttractionUpdate.getTime());
        	planAttractionService.updatePlanAttraction(planAttraction);
            return responseDto.success(planAttraction);
        } catch (Exception e) {
            e.printStackTrace();
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
