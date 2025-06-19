package com.enjoytrip.repository.verificationCodeRepository;

import com.enjoytrip.domain.verificationCode.SignUpVerificationCode;
import org.springframework.data.repository.CrudRepository;

public interface SignUpVerificationCodeRepository extends CrudRepository<SignUpVerificationCode, String>  {
}
