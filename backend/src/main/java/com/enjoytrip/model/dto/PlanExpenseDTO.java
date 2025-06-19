package com.enjoytrip.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PlanExpenseDTO {
    private int expId;
    private int planId;
    private String content;
    private int price;
    private Date regDt;
    private String receiptPath; // S3 이미지 URL
}
