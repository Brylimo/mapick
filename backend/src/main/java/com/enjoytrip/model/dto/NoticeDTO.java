package com.enjoytrip.model.dto;

import com.enjoytrip.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDTO {
    private long id;
    private String title;
    private String content;
    private int author;
    private Date createdAt;
    private Date updatedAt;

    private Member member;
}
