package com.nocountry.c930.service.impl;

import com.nocountry.c930.dto.CommentDto;
import com.nocountry.c930.dto.PostCommentDto;
import com.nocountry.c930.entity.CampaignEntity;
import com.nocountry.c930.entity.CommentEntity;
import com.nocountry.c930.entity.RoleEntity;
import com.nocountry.c930.entity.UserEntity;
import com.nocountry.c930.enumeration.RoleName;
import com.nocountry.c930.mapper.CommentMap;
import com.nocountry.c930.exception.ParamNotFound;
import com.nocountry.c930.repository.CampaignRepository;
import com.nocountry.c930.repository.CommentRepository;
import com.nocountry.c930.repository.RoleRepository;
import com.nocountry.c930.service.ICommentService;
import com.nocountry.c930.service.IUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    CommentRepository commentRepo;

    @Autowired
    CampaignRepository campaignRepo;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    CommentMap commentMap;

    @Autowired
    IUtilService util;


    @Override
    public CommentDto createComment(Long id, PostCommentDto dto) {

        UserEntity user = util.getLoggedUser();

        CampaignEntity campaign = campaignRepo.findById(id).orElseThrow(
                () -> new ParamNotFound("Campaign doesn't exist"));

        CommentEntity comment = new CommentEntity();
        comment.setDescription(dto.getText());
        comment.setUser(user);
        comment.setCampaign(campaign);

        return commentMap.commentEntity2Dto(commentRepo.save(comment));

    }

    @Override
    public CommentDto getComment(Long id) {

        CommentEntity comment = commentRepo.findById(id).orElseThrow(
                () -> new ParamNotFound("Comment doesn't exist"));

        return commentMap.commentEntity2Dto(comment);
    }

    @Override
    public CommentDto updateComment(Long id, CommentDto dto) {

        if (!commentRepo.existsById(id)) {
            throw new ParamNotFound("Comment doesn't exist");
        } else {
            CommentEntity entity = commentMap.commentDto2Entity(dto);
            entity.setCommentId(id);
        }

        return dto;
    }

    @Override
    public boolean deleteComment(Long idCampaign, Long idComment) {

        UserEntity userSession = util.getLoggedUser();
        RoleEntity admin = roleRepo.findByName(RoleName.ROLE_ADMIN);

        CampaignEntity campaign = campaignRepo.findById(idCampaign).orElseThrow(
                () -> new ParamNotFound("Campaign doesn't exist"));

        CommentEntity comment = commentRepo.findById(idComment).orElseThrow(
                () -> new ParamNotFound("Comment doesn't exist"));

        if (userSession != comment.getUser() && comment.getUser().getRole() != admin) {
            return false;
        } else {

            campaign.getCommentsReceived().remove(comment);
            commentRepo.delete(comment);
            return true;
        }
    }

    @Override
    public Set<CommentDto> getCampaignComments(Long id) {

        CampaignEntity campaign = campaignRepo.findById(id).orElseThrow(
                () -> new ParamNotFound("Campaign doesn't exist"));

        return commentMap.commentEntityList2DtoList(campaign.getCommentsReceived());
    }
}
