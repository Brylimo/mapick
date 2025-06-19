package com.enjoytrip.dto.community;

import lombok.*;

public class CommunityMemberRequestDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityMemberReview {
        private Integer communityId;
        private String  nickname;
        private String  name;
        private String  reason;
    }
}
