package com.enjoytrip.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityAttractionDTO {
    private int communityAttractionId;
    private int communityId;
    private int attractionId;
    private Date regDt;
    private Date modDt;
    private AttractionDTO attraction;
    private int likeCnt;
    private boolean liked;

    private List<TagDTO> tags;
}