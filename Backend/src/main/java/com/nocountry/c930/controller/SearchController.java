package com.nocountry.c930.controller;

import com.nocountry.c930.dto.CampaignBasicDto;
import com.nocountry.c930.enumeration.CampaignCategory;
import com.nocountry.c930.service.ICampaignService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campaigns/search")
@CrossOrigin
public class SearchController {

    @Autowired
    ICampaignService campaignService;


    @GetMapping()
    @ApiOperation(value = "Searches between all campaign by keyword")
    public List<CampaignBasicDto> searchAllByKeyword(@RequestParam(required = false) String keyword) {

        return campaignService.searchCampaignsByKeyword(keyword);
    }

    @GetMapping("/services")
    @ApiOperation(value = "Search services campaigns by keyword")
    public List<CampaignBasicDto> searchServicesCampaigns(@RequestParam(required = false) String keyword) {

        return campaignService.searchCampaignByCategoryAndKeyword(keyword, CampaignCategory.SERVICE);
    }

    @GetMapping("/products")
    @ApiOperation(value = "Search product campaigns by keyword")
    public List<CampaignBasicDto> searchProductCampaigns(@RequestParam(required = false) String keyword) {

        return campaignService.searchCampaignByCategoryAndKeyword(keyword, CampaignCategory.PRODUCT);
    }

    @GetMapping("/nearGoal")
    @ApiOperation(value = "List all the campaigns with 80% or more completion")
    public List<CampaignBasicDto> getByNearGoal() {

        return campaignService.listCampaignsNearGoal();
    }

    @GetMapping("/mostPopular")
    @ApiOperation(value = "Filters all the campaign by donations recieved")
    public List<CampaignBasicDto> getByMostPopular(@RequestParam(required = false) String keyword) {

        return campaignService.listCampaignsByMostPopular(keyword);
    }

    @GetMapping("/newest")
    @ApiOperation(value = "Gets all the campaigns by Date")
    public List<CampaignBasicDto> getByNewest() {

        return campaignService.listCampaignsByDate("DESC");
    }

}
