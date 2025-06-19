package com.enjoytrip.service.communityMember;

import com.enjoytrip.dto.community.CommunityMemberRequestDto;
import com.enjoytrip.model.dao.CommunityJoinRequestDAO;
import com.enjoytrip.model.dao.CommunityMemberDAO;
import com.enjoytrip.model.dto.CommunityJoinRequestDTO;
import com.enjoytrip.model.dto.CommunityMemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityMemberServiceImpl implements CommunityMemberService {
    private final CommunityMemberDAO communityMemberDAO;
    private final CommunityJoinRequestDAO communityJoinRequestDAO;

    @Override
    public void createJoinRequest(CommunityJoinRequestDTO communityJoinRequestDTO) {
        communityJoinRequestDAO.insertJoinRequest(communityJoinRequestDTO);
    }

    @Override
    public List<CommunityJoinRequestDTO> getRequestsByCommunityId(int communityId) {
        return communityJoinRequestDAO.getRequestsByCommunityId(communityId);
    }

    @Override
    public void approveJoinRequest(int joinRequestId) {
        CommunityJoinRequestDTO request = communityJoinRequestDAO.findById(joinRequestId);

        // community_member에 추가
        CommunityMemberDTO member = CommunityMemberDTO.builder()
                .memberId(request.getMemberId())
                .communityId(request.getCommunityId())
                .build();
        communityMemberDAO.insertCommunityMember(member);

        // 신청 삭제
        communityJoinRequestDAO.deleteById(joinRequestId);
    }

    @Override
    public void rejectJoinRequest(int joinRequestId) {
        communityJoinRequestDAO.deleteById(joinRequestId);
    }

	@Override
	public boolean isMember(int communityId, int memberId) {
		return communityMemberDAO.isMember(communityId, memberId);
	}

}
