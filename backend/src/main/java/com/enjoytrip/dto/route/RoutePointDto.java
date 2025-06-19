package com.enjoytrip.dto.route;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoutePointDto {
    private double lat;
    private double lng;
    private String attractionName;
}
