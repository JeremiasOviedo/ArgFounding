package com.nocountry.c930.dto;

import com.nocountry.c930.enumeration.CampaignCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
public class CampaignCreationDto {

    @NotBlank(message = "Campaigns name must not be blank")
    private String name;

    private String shortDescription;

    private String longDescription;

    @NotNull(message = "Campaigns closing date must not be blank")
    private Date closingDate;

    @NotNull(message = "Campaigns goal money cannot be null")
    private BigDecimal goalMoney;

    private MultipartFile image;

    private MultipartFile[] descriptionImages;

   @NotEmpty(message = "Must contain at least one donation tier")
    private List<@Valid TierCreationDto> donationTiers;


}
