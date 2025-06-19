package com.enjoytrip.service;

import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.user.UserRequestDto;
import com.enjoytrip.dto.user.UserResponseDto;
import com.enjoytrip.security.service.provider.TokenProvider;
import com.enjoytrip.service.token.RefreshTokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enjoytrip.model.dao.MemberDAO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
	private final PasswordEncoder passwordEncoder;

	private final MemberDAO memberDao;
	private final RefreshTokenService refreshTokenService;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final TokenProvider tokenProvider;

	@Override
	public Member getByCredentials(final String email, final String password) {
		final Member user = memberDao.getMemberByEmail(email);

		if (user != null && passwordEncoder.matches(password, user.getPassword())) {
			return user;
		}
		return null;
	}

	@Override
	@Transactional
	public UserResponseDto.TokenInfo login(UserRequestDto.Login userLoginRequestDto) {
		Member user = getByCredentials(userLoginRequestDto.getEmail(), userLoginRequestDto.getPassword());

		if (user == null) {
			return null;
		}

		UsernamePasswordAuthenticationToken authenticationToken = userLoginRequestDto.toAuthentication();
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken); // 실제 검증
		UserResponseDto.TokenInfo tokenInfo = tokenProvider.generateTokens(authentication);

		tokenInfo.setUserInfo(UserResponseDto.UserInfo.builder()
				.mno(user.getMno())
				.name(user.getName())
				.email(user.getEmail())
				.role(user.getRole())
				.build());

		refreshTokenService.writeTokenInfo(user.getEmail(), tokenInfo.getRefreshToken(), tokenInfo.getAccessToken());
		return tokenInfo;
	}

	@Override
	@Transactional
	public void memberJoin(Member member){
		memberDao.insertMember(member);
	}

	@Override
	@Transactional
	public Member getMemberByEmail(String email) {
		return memberDao.getMemberByEmail(email);
	}

	@Override
	public Member getMemberByName(String name) {
		return memberDao.getMemberByName(name);
	}

	@Override
	@Transactional
	public void memberUpdate(UserRequestDto.Register member) {
        memberDao.updateMember(member);
	}

	@Override
	@Transactional
	public boolean memberDelete(Integer id) {
		Member member = memberDao.getMemberById(id);

		if (member != null) {
			// todo hotplace도 delete하도록 처리하기 -> on delete cascade
			memberDao.deleteMember(id);
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public Member getMemberById(int memberId) {
		return memberDao.getMemberById(memberId);
	}

	@Override
	@Transactional
	public Member getMemberByEmailAndName(String name, String email) {
		return memberDao.getMemberByEmailAndName(name, email);
	}

}
