package com.nocountry.c930.service.impl;

import com.nocountry.c930.auth.dto.UserRegistrationDto;
import com.nocountry.c930.dto.PageDto;
import com.nocountry.c930.dto.UserDto;
import com.nocountry.c930.entity.RoleEntity;
import com.nocountry.c930.entity.UserEntity;
import com.nocountry.c930.enumeration.RoleName;
import com.nocountry.c930.mapper.UserMap;
import com.nocountry.c930.exception.ParamNotFound;
import com.nocountry.c930.repository.RoleRepository;
import com.nocountry.c930.repository.UserRepository;
import com.nocountry.c930.service.IUserService;
import com.nocountry.c930.service.IUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private IUtilService util;

    @Autowired
    private UserMap userMap;


    @Override
    public PageDto<UserDto> listAllUsers(Pageable page, HttpServletRequest request) {
        PageDto<UserDto> pageDto = new PageDto<>();
        Map<String, String> links = new HashMap<>();
        List<UserDto> listDto = new ArrayList<>();
        Page<UserEntity> elements = userRepo.findAll(page);

        elements.getContent().forEach(element -> listDto.add(userMap.userEntity2Dto(element)));
        links.put("next", elements.hasNext() ? util.makePaginationLink(request, page.getPageNumber() + 1) : "");
        links.put("previous", elements.hasPrevious() ? util.makePaginationLink(request, page.getPageNumber() - 1) : "");

        pageDto.setContent(listDto);
        pageDto.setLinks(links);

        return pageDto;
    }

    @Override
    public UserDto getUser(Long id) {
        UserEntity entity = userRepo.findById(id).orElseThrow(
                () -> new ParamNotFound("User doesn't exist"));

        return userMap.userEntity2Dto(entity);
    }

    @Override
    public boolean deleteUser(Long id) {

        UserEntity user = userRepo.findById(id).orElseThrow(
                () -> new ParamNotFound("User doesn't exist")
        );

        UserEntity loggedUser = util.getLoggedUser();
        RoleEntity admin = roleRepo.findByName(RoleName.ROLE_ADMIN);


        if (loggedUser.getUserId() != id && user.getRole() != admin) {
            return false;
        }
        RoleEntity role = user.getRole();
        role.getUsers().remove(user);
        userRepo.delete(user);
        return true;

    }

    @Override
    public UserDto updateUser(Long id, UserRegistrationDto dto) {

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        UserEntity entity = userRepo.findById(id).orElseThrow(
                () -> new ParamNotFound("User ID is invalid"));


        UserEntity loggedUser = util.getLoggedUser();
        RoleEntity admin = roleRepo.findByName(RoleName.ROLE_ADMIN);

        if (loggedUser != entity && loggedUser.getRole() != admin) {
            throw new RuntimeException("You can only update your own information");
        }


        if (dto.getFirstName() != null) {
            entity.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            entity.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null) {
            entity.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        }


        UserEntity entitySaved = userRepo.save(entity);

        return userMap.userEntity2Dto(entitySaved);
    }
}
