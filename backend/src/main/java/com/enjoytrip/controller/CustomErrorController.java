package com.enjoytrip.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class CustomErrorController extends BasicErrorController {
    public CustomErrorController(ErrorAttributes errorAttributes,
                                 ServerProperties serverProperties,
                                 List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, serverProperties.getError(), errorViewResolvers);
    }

    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mnv = super.errorHtml(request, response);
        HttpStatus hs = getStatus(request);
        switch (hs) {
            case NOT_FOUND:
                mnv.setViewName("error/error");
                break;
            case INTERNAL_SERVER_ERROR:
                mnv.setViewName("error/error");
                break;
            default:
                mnv.setViewName("error/error");
                break;
        }
        log.debug("view: {}, model: {}", mnv.getViewName(), mnv.getModel());
        return mnv;
    }

    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        return super.error(request);
    }
}
