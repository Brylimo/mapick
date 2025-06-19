package com.enjoytrip.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.dto.email.EmailResponseDto;
import com.enjoytrip.dto.user.UserRequestDto;
import com.enjoytrip.dto.user.UserRequestDto.Register;
import com.enjoytrip.dto.user.UserResponseDto;
import com.enjoytrip.service.MemberService;
import com.enjoytrip.service.verificationCode.VerificationCodeService;
import com.enjoytrip.util.EmailUtil;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
	private final MemberService mService;
	private final VerificationCodeService verificationCodeService;

	private final PasswordEncoder passwordEncoder;

	private final EmailUtil emailUtil;
	private final ResponseDto responseDto;

	@PostMapping
	@ResponseBody
	public ResponseEntity<?> memberJoin(@RequestBody Member member) {
		try {
			mService.memberJoin(member);
			member = mService.getMemberByEmail(member.getEmail());

			// 회원가입 확인 이메일 메시지 발송
			EmailResponseDto.EmailMessage emailMessage = EmailResponseDto.EmailMessage.builder()
					.to(member.getEmail())
					.subject("[Mapick] 회원가입을 축하드립니다.")
					.message(member.getName())
					.build();
			emailUtil.sendMail(emailMessage, "email/signup_confirm");

			return responseDto.success(member);
		}catch (DataAccessException e) {
			e.printStackTrace();
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping
	@ResponseBody
	public ResponseEntity<?> memberDetail(@AuthenticationPrincipal Member member) {
		try {
			return responseDto.success(member);
		} catch(Exception e) {
			e.printStackTrace();
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping
	@ResponseBody
	public ResponseEntity<?> memberUpdate(@RequestBody UserRequestDto.Register userRegisterRequestDto, @AuthenticationPrincipal Member member) {
		try {
			// 비밀번호가 맞나 확인
			if (!passwordEncoder.matches(userRegisterRequestDto.getPassword(), member.getPassword())) {
				return responseDto.fail("일치하는 회원이 없습니다.", HttpStatus.UNAUTHORIZED);
			}

			String role = userRegisterRequestDto.getRole();

			// 비밀번호가 맞음 -> 비밀번호를 변경하면 안되므로 null로 변경
			userRegisterRequestDto.setPassword(null);
			userRegisterRequestDto.setRole(null);
			mService.memberUpdate(userRegisterRequestDto);

			return responseDto.success(UserResponseDto.UserInfo.builder()
					.email(userRegisterRequestDto.getEmail())
					.name(userRegisterRequestDto.getName())
					.role(role)
					.build());

		} catch (DataAccessException e) {
			e.printStackTrace();
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> memberDelete(@PathVariable("id") Integer id) {
		boolean isDeleted = mService.memberDelete(id);
		if (isDeleted) {
			return responseDto.success(HttpStatus.OK);
		} else {
			return responseDto.success(HttpStatus.NOT_FOUND);
		}
	}


	@PostMapping("/memberFindPw")
	@ResponseBody
	public ResponseEntity<?> memberFindPw(@RequestBody UserRequestDto.findPW userFindPwDto) {
	    try {
	        Member member = mService.getMemberByEmailAndName(userFindPwDto.getName(), userFindPwDto.getEmail());
	        if(member == null) {
	            return responseDto.fail("not found", HttpStatus.NOT_FOUND);
	        }

	        return responseDto.success(member); // 성공만 반환해도 됨
	    } catch (Exception e) {
	        e.printStackTrace();
	        return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}



	@PutMapping("/memberChangePw")
	@ResponseBody
	public ResponseEntity<?> memberChangePw(@RequestBody UserRequestDto.changePW userChangepwDto) {
		try {
			UserRequestDto.Register updateMember = new Register();
			if(userChangepwDto.getPassword().equals(userChangepwDto.getPassword2())) {
				updateMember.setEmail(userChangepwDto.getEmail());
				updateMember.setPassword(userChangepwDto.getPassword());
				mService.memberUpdate(updateMember);
				Member member = mService.getMemberByEmail(updateMember.getEmail());
				return responseDto.success(member);
			}else {
				return responseDto.fail("비밀번호가 옳바르지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}catch (DataAccessException e) {
			e.printStackTrace();
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "회원가입/비밀번호 변경 인증코드 검증", description = "회원가입/비밀번호 변경 시 이메일 인증코드를 검증합니다.")
	@PostMapping(value = "/verification-code", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> verifyCode(@RequestBody UserRequestDto.VerifyCode verifyCode) {
		try {
			boolean isVerified = verificationCodeService.verifySignUpVerificationCode(verifyCode);

			if (isVerified) {
				return responseDto.success();
			} else {
				return responseDto.fail("인증코드가 맞지 않습니다.", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.debug("verificationCode error occurred!");
			return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
