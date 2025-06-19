package com.enjoytrip.dto.community;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class CommunityAttractionResponseDto {

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class PopularAttractionInfo {
        private int communityAttractionId;
        private int communityId;
        private int attractionId;
        private int likeCount;
        private double latitude;
        private double longitude;
    }
}
