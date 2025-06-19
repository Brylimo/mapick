package com.enjoytrip.service;

import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.user.UserRequestDto;
import com.enjoytrip.dto.user.UserResponseDto;

public interface MemberService {
	UserResponseDto.TokenInfo login(UserRequestDto.Login userLoginRequestDto);
	Member getByCredentials(final String username, final String password);
	void memberJoin(Member member);
	Member getMemberByEmail(String email);
	void memberUpdate(UserRequestDto.Register member);
	boolean memberDelete(Integer id);
    Member getMemberById(int memberId);
	Member getMemberByName(String name);
	Member getMemberByEmailAndName(String name, String email);
}