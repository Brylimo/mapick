package com.enjoytrip.model.dto;

import java.util.Date;
import java.util.List;

import com.enjoytrip.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResDTO {
    private long id;
    private int communityId;
    private String title;
    private String content;
    private int author;
    private Date createdAt;
    private Date updatedAt;
    private int likeCnt;
    private boolean liked;

    private Member member;
    private List<CommentDTO> comments;
}
