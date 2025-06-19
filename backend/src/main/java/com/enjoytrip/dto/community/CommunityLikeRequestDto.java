package com.enjoytrip.dto.community;

import lombok.*;

public class CommunityLikeRequestDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityLikePackage {
        private Integer communityId;
    }
}
