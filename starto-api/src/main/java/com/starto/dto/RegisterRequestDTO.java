package com.starto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Fix #3: proper DTO for /api/auth/register with @NotBlank validation.
 * The controller must annotate the parameter with @Valid to activate bean validation.
 */
@Data
public class RegisterRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone must be a valid number")
    private String phone;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(founder|investor|mentor|partner|talent)$",
             message = "Role must be one of: founder, investor, mentor, partner, talent")
    private String role;

    private String city;
    private String state;
    private String country;
    private String gender;
    private String bio;
    private String avatarUrl;
    private BigDecimal lat;
    private BigDecimal lng;
    private String address;
}