package com.enjoytrip.model.dao;

import com.enjoytrip.model.dto.CommunityJoinRequestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommunityJoinRequestDAO {
    void insertJoinRequest(CommunityJoinRequestDTO communityJoinRequestDTO);

    List<CommunityJoinRequestDTO> getRequestsByCommunityId(int communityId);

    CommunityJoinRequestDTO findById(int joinRequestId);

    void deleteById(int joinRequestId);
}
