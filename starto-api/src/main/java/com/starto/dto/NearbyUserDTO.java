package com.starto.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearbyUserDTO {
    public NearbyUserDTO(UUID id, String username, BigDecimal lat, BigDecimal lng) {
        this.id = id;
        this.username = username;
        this.lat = lat;
        this.lng = lng;
    }
    private UUID id;
    private String username;
    private String name;
    private String role;
    private String city;
    private String country;
    private String bio;
    private String industry;
    private String subIndustry;
    private BigDecimal lat;
    private BigDecimal lng;
    private Integer signalCount;
    private Integer networkSize;
    private String avatarUrl;
}

