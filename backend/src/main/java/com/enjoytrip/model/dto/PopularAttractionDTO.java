package com.enjoytrip.model.dto;

import lombok.Data;

@Data
public class PopularAttractionDTO {
    private int communityAttractionId;
    private int communityId;
    private int attractionId;
    private int likeCount;
    private double latitude;
    private double longitude;
    private String title;
    private String tel;
    private String addr1;
}
