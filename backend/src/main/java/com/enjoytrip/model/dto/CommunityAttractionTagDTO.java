package com.enjoytrip.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityAttractionTagDTO {
    private int communityAttractionTagId;
    private int communityAttractionId;
    private int tagId;
    private Date regDt;
    private Date modDt;
}
