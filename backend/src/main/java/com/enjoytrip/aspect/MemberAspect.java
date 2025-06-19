package com.enjoytrip.aspect;

import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.user.UserRequestDto;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class MemberAspect {
	private final PasswordEncoder pe;
    @Before("execution( * com.enjoytrip..dao..insertMember(com.enjoytrip.domain.member.Member)) && args(member)")
    public void endcodeMemberPasswordForInsert(Member member) {
        encodeIfNeeded(member);
    }

    // 회원정보 업데이트 시 (Member 객체 직접 수정 시)
    @Before("execution(* com.enjoytrip..*.memberUpdate(..)) && args(member)")
    public void encodePasswordOnUpdateMember(Member member) {
        encodeIfNeeded(member);
    }

    // 회원정보 업데이트 시 (DTO 기반)
    @Before("execution(* com.enjoytrip..*.memberUpdate(..)) && args(dto,..)")
    public void encodePasswordOnUpdateDto(UserRequestDto.Register dto) {
        String rawPassword = dto.getPassword();
        if (rawPassword != null && !rawPassword.isBlank() && !rawPassword.startsWith("{bcrypt}")) {
            log.debug("Before encoding (DTO): {}", rawPassword);
            dto.setPassword(pe.encode(rawPassword));
            log.debug("After encoding (DTO): {}", dto.getPassword());
        }
    }

    private void encodeIfNeeded(Member member) {
        String rawPassword = member.getPassword();
        if (rawPassword != null && !rawPassword.isBlank() && !rawPassword.startsWith("{bcrypt}")) {
            log.debug("Before encoding (Member): {}", rawPassword);
            member.setPassword(pe.encode(rawPassword));
            log.debug("After encoding (Member): {}", member.getPassword());
        }
    }
	
}
