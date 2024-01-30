package com.nocountry.c930.mapper;

import com.nocountry.c930.dto.DonationTierDto;
import com.nocountry.c930.dto.TierCreationDto;
import com.nocountry.c930.entity.DonationTierEntity;
import com.nocountry.c930.service.impl.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class DonationTierMap {

    @Autowired
    StorageService storageService;

    public DonationTierDto tierEntity2Dto(DonationTierEntity entity) {
        DonationTierDto dto = new DonationTierDto();

        dto.setTierName(entity.getTierName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setTierId(entity.getDonationTierId());
        dto.setImageUrl(entity.getImageUrl());

        return dto;
    }

    public DonationTierEntity tierDto2Entity(TierCreationDto dto) throws IOException {

        DonationTierEntity entity = new DonationTierEntity();

        entity.setTierName(dto.getTierName());
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());
        entity.setLimited(dto.isLimited());

        if (dto.getImage() != null) {

            entity.setImageUrl(storageService.uploadImage(dto.getImage()));

        } else {

            entity.setImageUrl("https://argfunding.s3.sa-east-1.amazonaws.com/donation_tier_placeholder.png");
        }

        if (dto.isLimited()) {
            entity.setStockLimit(dto.getStockLimit());
        }

        return entity;
    }

    public Set<DonationTierEntity> tierDtoSet2Entity(Set<TierCreationDto> dtos) throws IOException {

        Set<DonationTierEntity> entities = new HashSet<>();

        for (TierCreationDto dto : dtos) {

            entities.add(tierDto2Entity(dto));
        }

        return entities;
    }

    public Set<DonationTierDto> tierEntitySet2Dto(Set<DonationTierEntity> entites) {

        Set<DonationTierDto> dtos = new HashSet<>();

        for (DonationTierEntity entity : entites) {

            dtos.add(tierEntity2Dto(entity));
        }

        return dtos;

    }

}
