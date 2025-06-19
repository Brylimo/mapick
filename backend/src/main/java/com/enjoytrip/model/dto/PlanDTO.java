package com.enjoytrip.model.dto;

import com.enjoytrip.domain.member.Member;
import lombok.Data;

import java.util.Date;

@Data
public class PlanDTO {
    private int pid;
    private String name;
    private int mno;
    private Date createdDate;
    private Date updatedDate;

    private Member member;
}
