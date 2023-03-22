package com.nocountry.c930.auth.service;


import com.nocountry.c930.auth.dto.ResponseUserDto;
import com.nocountry.c930.auth.dto.UserRegistrationDto;
import com.nocountry.c930.entity.RoleEntity;
import com.nocountry.c930.entity.UserEntity;
import com.nocountry.c930.enumeration.RoleName;
import com.nocountry.c930.mapper.UserMap;
import com.nocountry.c930.exception.RepeatedUsername;
import com.nocountry.c930.repository.RoleRepository;
import com.nocountry.c930.repository.UserRepository;
import com.nocountry.c930.service.impl.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.io.IOException;

@Service
public class UserDetailsCustomService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private StorageService storageService;
    @Autowired
    private UserMap userMap;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepo.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException("username or password not found");
        }
        return UserDetailsImpl.build(userEntity);
    }

    public ResponseUserDto save(@Valid UserRegistrationDto userDto) throws RepeatedUsername {

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        if (userRepo.findByEmail(userDto.getEmail()) != null) {
            throw new RepeatedUsername("email already exist");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        if (userDto.getImage() != null) {

            try {
                userEntity.setImageUrl(storageService.uploadImage(userDto.getImage()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            userEntity.setImageUrl("https://argfoundingimages.nyc3.cdn.digitaloceanspaces.com/User_Placeholder.png");
        }


        if (!roleRepo.existsByName(RoleName.ROLE_ADMIN)) {

            RoleEntity roleAdmin = new RoleEntity();
            roleAdmin.setName(RoleName.ROLE_ADMIN);

            roleRepo.save(roleAdmin);

            RoleEntity roleUser = new RoleEntity();
            roleUser.setName(RoleName.ROLE_USER);

            roleRepo.save(roleUser);


        }

        RoleEntity role = roleRepo.findByName(RoleName.ROLE_USER);
        userEntity.setRole(role);

        UserEntity entitySaved = this.userRepo.save(userEntity);


        ResponseUserDto responseUserDto = userMap.userAuthEntity2Dto(entitySaved);


        return responseUserDto;


    }

    public ResponseUserDto saveAdmin(@Valid UserRegistrationDto userDto) throws RepeatedUsername {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        if (userRepo.findByEmail(userDto.getEmail()) != null) {
            throw new RepeatedUsername("email already exist");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        userEntity.setRole(roleRepo.findByName(RoleName.ROLE_ADMIN));

        UserEntity entitySaved = this.userRepo.save(userEntity);


        ResponseUserDto responseUserDto = userMap.userAuthEntity2Dto(entitySaved);


        return responseUserDto;

    }


}
