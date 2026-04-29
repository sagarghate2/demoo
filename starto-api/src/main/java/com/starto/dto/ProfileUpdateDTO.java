package com.starto.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProfileUpdateDTO {
    private String name;
    private String username;
    private String bio;
    private String avatarUrl;
    private String websiteUrl;
    private String linkedinUrl;
    private String twitterUrl;
    private String githubUrl;
    private String city;
    private String state;
    private String role;
    private String industry;
    private String subIndustry;
    private String phone;
    private BigDecimal lat;
    private BigDecimal lng;
    private String address;
}
