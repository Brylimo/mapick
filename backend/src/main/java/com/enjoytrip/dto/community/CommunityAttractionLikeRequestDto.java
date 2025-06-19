package com.enjoytrip.dto.community;

import lombok.*;

public class CommunityAttractionLikeRequestDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityAttractionLikePackage {
        private Integer communityAttractionId;
    }
}