package com.nocountry.c930.controller;

import com.nocountry.c930.auth.dto.UserRegistrationDto;
import com.nocountry.c930.dto.CampaignBasicDto;
import com.nocountry.c930.dto.PageDto;
import com.nocountry.c930.dto.UserDto;
import com.nocountry.c930.repository.UserRepository;
import com.nocountry.c930.service.ICampaignService;
import com.nocountry.c930.service.IUserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ICampaignService campaignService;


    @GetMapping("/{id}")
    @ApiOperation(value = "Get User Info",
            notes = "Gets all the user information, including tournaments that manages and teams that belongs to.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "User ID is invalid (must use numbers value only"),
            @ApiResponse(code = 404, message = "User not found")})
    public UserDto getUser(@PathVariable(name = "id") Long idUser) {

        return userService.getUser(idUser);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ApiOperation(value = "Delete an user",
            notes = "Deletes an user, you can only delete your own user unless you are an Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "User ID is invalid (User number values only)"),
            @ApiResponse(code = 401, message = "You can only delete your own user"),
            @ApiResponse(code = 404, message = "User not found")})
    public ResponseEntity<String> deleteUser(@PathVariable(name = "id") Long idUser) {

        if (userService.deleteUser(idUser)) {
            return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
        } else {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User cannot be deleted");
        }

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ApiOperation(value = "Updates your Info",
            notes = "Updates the user information, only can update your own information")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "User ID is invalid (User number values only)"),
            @ApiResponse(code = 401, message = "You can only update your own information"),
            @ApiResponse(code = 404, message = "User not found")})
    public UserDto updateUser(@PathVariable(name = "id") Long idUser, @RequestBody UserRegistrationDto dto) {

        return userService.updateUser(idUser, dto);
    }

    @GetMapping("/{id}/campaigns")
    @ApiOperation(value = "Gets all the campaigns made by an user")
    public List<CampaignBasicDto> getCampaignByUser(@PathVariable(name = "id") Long idUser) {

        return campaignService.listCampaignByUser(idUser);
    }


    @GetMapping()
    @ApiOperation(value = "List All Users",
            notes = "Gives you a paginated list of all the users, only administrators can use this endpoint")
    public PageDto<UserDto> getAllUsers(@PageableDefault(size = 5) Pageable page,
                                        HttpServletRequest request) {

        return userService.listAllUsers(page, request);
    }


}
