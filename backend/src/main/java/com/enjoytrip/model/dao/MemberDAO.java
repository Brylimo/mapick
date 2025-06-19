package com.enjoytrip.model.dao;


import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.user.UserRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberDAO {
	void insertMember(Member member);

	Member getMemberByEmailAndPassword(@Param("email") String email, @Param("password") String password);
	
	Member getMemberByEmail(String email);
	
	void updateMember(UserRequestDto.Register member);
	
	void deleteMember(Integer id);

    Member getMemberById(int memberId);

	Member getMemberByName(String name);

	Member getMemberByEmailAndName(String name, String email);
}
