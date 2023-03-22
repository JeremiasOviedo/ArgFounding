package com.nocountry.c930.controller;

import com.nocountry.c930.dto.CampaignBasicDto;
import com.nocountry.c930.service.ICampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/profile")
public class MyUserController {


    @Autowired
    ICampaignService campaignService;

    @GetMapping("/campaigns")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<CampaignBasicDto> getMyCampaigns() {

        return campaignService.getMyCampaigns();


    }
}
