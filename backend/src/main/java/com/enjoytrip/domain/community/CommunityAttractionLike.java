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
public class CommunityAttractionLike {
    private int communityAttractionLikeId;
    private int communityAttractionId;
    private int memberId;
    private Date regDt;
    private Date modDt;
}
