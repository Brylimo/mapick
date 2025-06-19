package com.enjoytrip.controller;

import com.enjoytrip.model.dto.GugunsDTO;
import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.service.GugunsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/guguns")
@RequiredArgsConstructor
public class GugunController {
    private final GugunsService gugunsService;

    private final ResponseDto responseDto;

    @GetMapping
    @ResponseBody
    private ResponseEntity<?> getGugunsData(@RequestParam Integer sidoCode) {
        try {
            List<GugunsDTO> guguns = gugunsService.getGugunsBySido(sidoCode);

            return responseDto.success(guguns);
        } catch (Exception e) {
            log.debug("getGugunsData error occurred!");
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
