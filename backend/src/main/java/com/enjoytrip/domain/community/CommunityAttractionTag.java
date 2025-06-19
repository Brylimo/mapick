package com.enjoytrip.domain.community;

import com.enjoytrip.model.dto.TagDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityAttractionTag {
    private int communityAttractionTagId;
    private int communityAttractionId;
    private int tagId;
    private Date regDt;
    private Date modDt;

    private TagDTO tag;
}
