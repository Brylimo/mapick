package com.enjoytrip.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class HotplaceDTO {
    private int hpid;
    private int mno;
    private String name;
    private String tel;
    private String address;
    private Date createdDate;
    private int attractionId;
    private Double latitude;
    private Double longitude;
}
