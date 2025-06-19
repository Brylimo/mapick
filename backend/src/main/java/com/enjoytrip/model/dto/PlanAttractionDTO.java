package com.enjoytrip.model.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;


@Data
public class PlanAttractionDTO {
    private int ppid;
    private int idx;
    private int planid;
    private int attractionid;
    private String exp;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;
    private AttractionDTO attraction;
}
