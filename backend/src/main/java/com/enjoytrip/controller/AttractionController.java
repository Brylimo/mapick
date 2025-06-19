package com.enjoytrip.controller;

import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.model.dto.*;
import com.enjoytrip.service.AttractionService;
import com.enjoytrip.service.ContentTypeService;
import com.enjoytrip.service.GugunsService;
import com.enjoytrip.service.SidosService;
import com.enjoytrip.util.TourUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/attractions")
@RequiredArgsConstructor
public class AttractionController {
    private final GugunsService gugunsService;
    private final SidosService sidosService;
    private final ContentTypeService contentTypeService;
    private final AttractionService attractionService;
    private final TourUtil tourUtil;

    private final ResponseDto responseDto;

    
    @GetMapping("/sidos")
    @ResponseBody
    public ResponseEntity<?> getSidoList() {
        try {
            List<SidosDTO> sidos = sidosService.getAllSidos();
            return responseDto.success(sidos);
        } catch (Exception e) {
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/content-type")
    @ResponseBody
    public ResponseEntity<?> getContentTypeList() {
        try {
        	List<ContentTypeDTO> contentTypes = contentTypeService.getAllContentType();
            return responseDto.success(contentTypes);
        } catch (Exception e) {
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @ResponseBody
    private ResponseEntity<?> createAttraction(@RequestBody AttractionDataDTO attractionDTO) {
        try {
            attractionService.createAttraction(attractionDTO);

            return responseDto.success(attractionDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    @ResponseBody
    private ResponseEntity<?> getSearchedData(@RequestParam Integer region, @RequestParam Integer subregion, @RequestParam(required = false) Integer category) {
        try {
            AttractionDataDTO dto = new AttractionDataDTO();
            dto.setAreacode(region);
            dto.setSigungucode(subregion);
            dto.setContenttypeid(category);

            List<AttractionDataDTO> list = attractionService.getAttractionsByAttractionDTO(dto);
            return responseDto.success(list);
        } catch (Exception e) {
            log.debug("getSearchedData error occurred!");
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/keyword")
    @ResponseBody
    private ResponseEntity<?> getSearchedDataByKeyword(@RequestParam String keyword) {
        try {
            AttractionDataDTO attractionDataDTO = tourUtil.searchAttractionsByTitle(keyword);
            return responseDto.success(attractionDataDTO);
        } catch (Exception e) {
            log.debug("getSearchedDataByKeyword error occurred!");
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
