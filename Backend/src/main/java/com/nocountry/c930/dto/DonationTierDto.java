package com.nocountry.c930.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class DonationTierDto {

    private Long tierId;

    @NotBlank(message = "Tier name cannot be empty")
    private String tierName;

    @NotNull(message = "Tiers price cannot be null")
    private BigDecimal price;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    private String imageUrl;
}
