package com.enjoytrip.dto.community;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class CommunityLikeResponseDto {
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class CommunityLikeInfo {
        private int communityLikeId;
        private int memberId;
        private int communityId;
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class CheckCommunityLike {
        private boolean checked;
    }
}
