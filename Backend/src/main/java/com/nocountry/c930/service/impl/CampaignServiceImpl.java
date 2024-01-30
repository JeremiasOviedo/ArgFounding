package com.nocountry.c930.service.impl;

import com.nocountry.c930.dto.*;
import com.nocountry.c930.entity.*;
import com.nocountry.c930.enumeration.CampaignCategory;
import com.nocountry.c930.enumeration.CampaignStatus;
import com.nocountry.c930.enumeration.RoleName;
import com.nocountry.c930.mapper.CampaignMap;
import com.nocountry.c930.mapper.DonationMap;
import com.nocountry.c930.mapper.DonationTierMap;
import com.nocountry.c930.exception.NotAllowed;
import com.nocountry.c930.exception.ParamNotFound;
import com.nocountry.c930.repository.*;
import com.nocountry.c930.service.ICampaignService;
import com.nocountry.c930.service.IUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class CampaignServiceImpl implements ICampaignService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    CampaignRepository campaignRepo;

    @Autowired
    StorageService storageService;

    @Autowired
    DonationMap donationMap;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    DonationTierRepository tierRepo;

    @Autowired
    CampaignMap campaignMap;

    @Autowired
    DonationTierMap tierMap;

    @Autowired
    private IUtilService util;

    @Override
    public CampaignBasicDto createCampaign(CampaignCreationDto dto) throws IOException {

        UserEntity user = util.getLoggedUser();

        CampaignEntity campaignEntity = campaignMap.campaignCreation2Entity(dto);
        campaignEntity.setCurrentMoney(BigDecimal.ZERO);
        campaignEntity.setStatus(CampaignStatus.OPEN);
        campaignEntity.setCreator(user);

        if (dto.getImage() != null) {
            campaignEntity.setBannerUrl(storageService.uploadImage(dto.getImage()));
        } else {

            campaignEntity.setBannerUrl("https://argfunding.s3.sa-east-1.amazonaws.com/Banner-Placeholder.png");
        }

        if (user.getImageUrl() != null) {
            campaignEntity.setLogoUrl(user.getImageUrl());
        }

        if (dto.getDescriptionImages() != null) {

            for (MultipartFile image : dto.getDescriptionImages()) {

                campaignEntity.addImagesToDescription(storageService.uploadImage(image));
            }
        }

        campaignEntity.setCategory(CampaignCategory.PRODUCT);

        campaignRepo.save(campaignEntity);


        Set<DonationTierEntity> tiers = new HashSet<>();

        for (TierCreationDto tierDto : dto.getDonationTiers()) {
            DonationTierEntity donationTierEntity = tierMap.tierDto2Entity(tierDto);
            DonationTierEntity entitySaved = tierRepo.save(donationTierEntity);

            if (donationTierEntity.getImageUrl() != null) {

            }else{
                donationTierEntity.setImageUrl("https://argfunding.s3.sa-east-1.amazonaws.com/donation_tier_placeholder.png");
            }
            donationTierEntity.setCampaign(campaignEntity);


            tiers.add(entitySaved);

        }
        campaignEntity.setDonationTiers(tiers);

        return campaignMap.campaignEntity2BasicDto(campaignRepo.save(campaignEntity));
    }

    @Override
    public PageDto<CampaignBasicDto> listAllCampaigns(Pageable page, HttpServletRequest request) {
        PageDto<CampaignBasicDto> pageDto = new PageDto<>();
        Map<String, String> links = new HashMap<>();
        List<CampaignBasicDto> listDto = new ArrayList<>();
        Page<CampaignEntity> elements = campaignRepo.findAllByStatus(CampaignStatus.OPEN, page);

        elements.getContent().forEach(element -> listDto.add(campaignMap.campaignEntity2BasicDto(element)));
        links.put("next", elements.hasNext() ? util.makePaginationLink(request, page.getPageNumber() + 1) : "");
        links.put("previous", elements.hasPrevious() ? util.makePaginationLink(request, page.getPageNumber() - 1) : "");

        pageDto.setContent(listDto);
        pageDto.setLinks(links);

        return pageDto;
    }

    @Override
    public Set<DonationDto> findAllDonations(Long id) {

        CampaignEntity campaign = campaignRepo.findById(id).orElseThrow(
                () -> new ParamNotFound("Campaign doesn't exist"));


        return donationMap.donationEntityList2DtoList(campaign.getDonationsReceived());
    }


    @Override
    public CampaignDto getCampaign(Long id) {

        CampaignEntity campaign = campaignRepo.findById(id).orElseThrow(
                () -> new ParamNotFound("Campaign doesn't exist"));

        return campaignMap.campaignEntity2Dto(campaign);
    }


    @Override
    public CampaignDto updateCampaign(Long id, CampaignCreationDto dto) throws IOException {

        UserEntity user = util.getLoggedUser();

        RoleEntity admin = roleRepo.findByName(RoleName.ROLE_ADMIN);

        CampaignEntity campaign = campaignRepo.findById(id).orElseThrow(
                () -> new ParamNotFound("Campaign doesn't exist"));

        if (campaign.getCreator() != user && user.getRole() != admin) {
            throw new NotAllowed("You don't have permission to do that");
        }

        campaign.setName(dto.getName());

        campaign.setLongDescription(dto.getLongDescription());
        campaign.setShortDescription(dto.getShortDescription());
        campaign.setGoalMoney(dto.getGoalMoney());

        if (dto.getImage() != null) {
            campaign.setBannerUrl(storageService.uploadImage(dto.getImage()));
        }

        if (dto.getDescriptionImages() != null) {
            for (MultipartFile image : dto.getDescriptionImages()) {
                campaign.getDescriptionImages().add(storageService.uploadImage(image));
            }

        }


        return campaignMap.campaignEntity2Dto(campaignRepo.save(campaign));

    }

    @Override
    public void deleteCampaign(Long id) {

        CampaignEntity campaign = campaignRepo.findById(id).orElseThrow(
                () -> new ParamNotFound("Campaign doesn't exist"));


        campaignRepo.delete(campaign);
    }

    @Override
    public void updateCampaignMoney(Long idCampaign) {

        CampaignEntity campaign = campaignRepo.findById(idCampaign).orElseThrow(
                () -> new ParamNotFound("Campaign doesn't exist")
        );

        BigDecimal currentMoney = new BigDecimal(0);

        for (DonationEntity donation : campaign.getDonationsReceived()) {
            currentMoney = currentMoney.add(donation.getAmount());
        }

        campaign.setCurrentMoney(currentMoney);
        campaignRepo.save(campaign);

    }

    @Override
    public List<CampaignBasicDto> listCampaignsNearGoal() {

        return campaignMap.campaignEntityList2BasicDto(campaignRepo.findCampaignsNearGoal());

    }

    @Override
    public List<CampaignBasicDto> getMyCampaigns() {

        UserEntity user = util.getLoggedUser();

        return listCampaignByUser(user.getUserId());
    }

    @Override
    public List<CampaignBasicDto> listCampaignsByDate(String order) {

        if (order.equals("ASC")) {
            return campaignMap.campaignEntityList2BasicDto(campaignRepo.findAllByOrderByCreationDateAsc());
        } else if (order.equals("DESC")) {
            return campaignMap.campaignEntityList2BasicDto(campaignRepo.findAllByOrderByCreationDateDesc());
        } else {

            return campaignMap.campaignEntityList2BasicDto(campaignRepo.findAll());
        }

    }

    @Override
    public List<CampaignBasicDto> searchCampaignsByKeyword(String keyword) {

        return campaignMap.campaignEntityList2BasicDto(campaignRepo.findAllByKeyword(keyword));


    }

    @Override
    public List<CampaignBasicDto> searchCampaignByCategoryAndKeyword(String keyword, CampaignCategory category) {

        if (category == null) {
            return campaignMap.campaignEntityList2BasicDto(campaignRepo.findAllByKeyword(keyword));
        }

        if (category == CampaignCategory.SERVICE) {
            return campaignMap.campaignEntityList2BasicDto(campaignRepo.findServicesByKeyword(keyword, CampaignCategory.SERVICE));
        } else {
            return campaignMap.campaignEntityList2BasicDto(campaignRepo.findServicesByKeyword(keyword, CampaignCategory.PRODUCT));

        }
    }

    @Override
    public List<CampaignBasicDto> listCampaignsByMostPopular(String keyword) {

        return campaignMap.campaignEntityList2BasicDto(campaignRepo.findALLByMostPopular(keyword));
    }

    @Override
    public List<CampaignBasicDto> listCampaignByUser(Long idUser) {

        UserEntity user = userRepo.findById(idUser).orElseThrow(
                () -> new ParamNotFound("User doesn't exist"));


        return campaignMap.campaignEntityList2BasicDto(user.getOwnedCampaigns().stream().toList());
    }

    @Override
    public void replaceDescriptionImages(Long idCampaign, UpdateImagesDto images) throws IOException {

        CampaignEntity campaign = campaignRepo.findById(idCampaign).orElseThrow(
                () -> new ParamNotFound("Campaign doesn't exist")
        );

        campaign.getDescriptionImages().removeAll(campaign.getDescriptionImages());

        if (images != null) {
            for (MultipartFile image : images.getDescriptionImages()) {
                campaign.getDescriptionImages().add(storageService.uploadImage(image));
                campaign.setBannerUrl(storageService.uploadImage(images.getBanner()));
                campaign.setLogoUrl(storageService.uploadImage(images.getLogo()));
            }

            campaignRepo.save(campaign);

        }

    }

    @Override
    public void replaceTierImages(Long idCampaign, UpdateTierImagesDto images) throws IOException {

        CampaignEntity campaign = campaignRepo.findById(idCampaign).orElseThrow(
                () -> new ParamNotFound("Campaign doesn't exist")
        );


        if (images != null) {
            int i = 0;
            List<DonationTierEntity> donationTiers = new ArrayList<>(campaign.getDonationTiers().stream().toList());

            Comparator<DonationTierEntity> compareById = (DonationTierEntity o1, DonationTierEntity o2) ->
                    o1.getDonationTierId().compareTo(o2.getDonationTierId());

            donationTiers.sort(compareById);

            for (MultipartFile image : images.getTierImages()) {

                DonationTierEntity tier = donationTiers.get(i);
                tier.setImageUrl(storageService.uploadImage(image));
                tierRepo.save(tier);

                i++;
            }
            campaignRepo.save(campaign);

        }

    }

}
