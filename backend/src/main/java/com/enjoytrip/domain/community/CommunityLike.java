package com.enjoytrip.domain.community;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityLike {
    private int communityLikeId;
    private int memberId;
    private int communityId;
    private Date regDt;
    private Date modDt;
}
