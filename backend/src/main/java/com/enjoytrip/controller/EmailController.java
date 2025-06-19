package com.enjoytrip.controller;

import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.dto.email.EmailRequestDto;
import com.enjoytrip.dto.email.EmailResponseDto;
import com.enjoytrip.util.EmailUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/email")
public class EmailController {
    private final EmailUtil emailUtil;
    private final ResponseDto responseDto;

    @Operation(summary = "임시 코드 발송 요청", description = "임시 코드를 이메일을 통해 발송합니다.")
    @PostMapping(value = "/verification-code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendCode(@RequestBody EmailRequestDto.SendCode sendCode) {
        try {
            EmailResponseDto.EmailMessage emailMessage = EmailResponseDto.EmailMessage.builder()
                    .to(sendCode.getEmail())
                    .subject("[Mapick] 회원가입 이메일 인증 안내")
                    .build();

            emailUtil.sendMail(emailMessage, "email/signup");

            return responseDto.success();
        } catch (Exception e) {
            log.debug("sendCode error occurred!");
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "임시 코드 발송 요청", description = "임시 코드를 이메일을 통해 발송합니다.")
    @PostMapping(value = "/verification-code/changePw", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendCode2(@RequestBody EmailRequestDto.SendCode sendCode) {
        try {
            EmailResponseDto.EmailMessage emailMessage = EmailResponseDto.EmailMessage.builder()
                    .to(sendCode.getEmail())
                    .subject("[Mapick] 비밀번호 변경 이메일 인증 안내")
                    .build();

            emailUtil.sendMail(emailMessage, "email/changePW");

            return responseDto.success();
        } catch (Exception e) {
            log.debug("sendCode error occurred!");
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
