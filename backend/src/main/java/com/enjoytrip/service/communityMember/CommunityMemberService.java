package com.enjoytrip.service.communityMember;

import com.enjoytrip.model.dto.CommunityJoinRequestDTO;

import java.util.List;

public interface CommunityMemberService {

    void createJoinRequest(CommunityJoinRequestDTO communityJoinRequestDTO);

    List<CommunityJoinRequestDTO> getRequestsByCommunityId(int communityId);

    void approveJoinRequest(int joinRequestId);

    void rejectJoinRequest(int joinRequestId);

	boolean isMember(int communityId, int memberId);
}
