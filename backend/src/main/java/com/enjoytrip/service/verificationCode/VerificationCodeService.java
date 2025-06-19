package com.enjoytrip.service.verificationCode;

import com.enjoytrip.dto.user.UserRequestDto;

public interface VerificationCodeService {
    void storeSignUpVerificationCode(String email, String code);
    boolean verifySignUpVerificationCode(UserRequestDto.VerifyCode verifyCode);
}
