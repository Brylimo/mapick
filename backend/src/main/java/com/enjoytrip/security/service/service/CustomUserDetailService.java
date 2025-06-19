package com.enjoytrip.security.service.service;

import com.enjoytrip.domain.member.Member;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.enjoytrip.model.dao.MemberDAO;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
	private final MemberDAO mDao;
	
	@Override
	@Transactional
	public Member loadUserByUsername(String email) throws UsernameNotFoundException {
		Member member = mDao.getMemberByEmail(email);
		if(member==null) {
			throw new UsernameNotFoundException(email);
		}
		return member;
	}

}
