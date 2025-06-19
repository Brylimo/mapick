package com.enjoytrip.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagDTO {
	private int tagId;
	private String name;
	private Date regDt;
	private Date modDt;
}