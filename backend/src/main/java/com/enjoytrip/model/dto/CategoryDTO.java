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
public class CategoryDTO {
	private int categoryId;
	private String categoryName;
	private Date regDt;
	private Date modDt;
}
