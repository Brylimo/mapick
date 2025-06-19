package com.enjoytrip.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enjoytrip.service.AiChatService;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ai")
@Slf4j
@RequiredArgsConstructor
public class AiChatController {
	private final AiChatService chatService;

    @PostMapping("/simple")
    ResponseEntity<?> simpleGeneration(@RequestBody Map<String, String> userInput) {
        Object result = chatService.simpleGeneration(userInput.get("message"));
        return handleSuccess(Map.of("message", result));
    }
    
    private ResponseEntity<?> handleSuccess(Object data) {
        return handleSuccess(data, HttpStatus.OK);
    }
    
    private ResponseEntity<?> handleSuccess(Object data, HttpStatus status) {
        Map<String, Object> map = Map.of("status", "SUCCESS", "data", data);
        return ResponseEntity.status(status).body(map);
    }
    
}
