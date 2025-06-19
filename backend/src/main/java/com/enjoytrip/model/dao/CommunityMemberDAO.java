package com.enjoytrip.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.enjoytrip.model.dto.CommunityMemberDTO;

@Mapper
public interface CommunityMemberDAO {
	void insertCommunityMember(CommunityMemberDTO communityMemberDTO);

	void deleteByCommunityId(int communityId);

	boolean isMember(int communityId, int memberId);
}
