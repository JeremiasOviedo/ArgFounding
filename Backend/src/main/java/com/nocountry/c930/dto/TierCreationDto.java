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
public class TierCreationDto {


    @NotBlank(message = "Tier name cannot be blank")
    private String tierName;

    @NotNull(message = "Tier price cannot be null")
    private BigDecimal price;

    @NotBlank(message = "You need to give the tier a description")
    private String description;

    private MultipartFile image;

    @NotNull(message = "You need to declare if the tier is stock limited")
    private boolean isLimited;

    private int stockLimit;
}
