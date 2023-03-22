package com.nocountry.c930.auth.controller;


import com.nocountry.c930.auth.dto.AuthenticationRequest;
import com.nocountry.c930.auth.dto.AuthenticationResponse;
import com.nocountry.c930.auth.dto.ResponseUserDto;
import com.nocountry.c930.auth.dto.UserRegistrationDto;
import com.nocountry.c930.auth.service.JwtUtils;
import com.nocountry.c930.auth.service.UserDetailsCustomService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {

    private final UserDetailsCustomService userDetailsServices;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtTokenUtils;


    @PostMapping("/register")
    @ApiOperation(value = "Register as a user",
            notes = "Create an account filling the form, password must be 8 or more characters long")
    public ResponseEntity<ResponseUserDto> signUp(@Valid @ModelAttribute UserRegistrationDto user) {
        ResponseUserDto userRegister = this.userDetailsServices.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userRegister);
    }


    @PostMapping("/registerAdmin")
    @ApiOperation(value = "Register as an Admin",
            notes = "Create an account filling the form, the role will be Admin in this case")
    public ResponseEntity<ResponseUserDto> signUpAdmin(@Valid @ModelAttribute UserRegistrationDto user) {

        ResponseUserDto userRegister = this.userDetailsServices.saveAdmin(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userRegister);
    }

    @PostMapping("/login")
    @ApiOperation(value = "Login using your email and password",
            notes = "This will retrieve a Json Web Token, granting you access to the endpoints, you need to copy and paste" +
                    " the JWT in the authorization section above the controllers, prepending the word Bearer before the token")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid credentials"),
            @ApiResponse(code = 404, message = "User not found")})
    public ResponseEntity<AuthenticationResponse> signIn(
          @Valid @RequestBody AuthenticationRequest authenticationRequest) {

        UserDetails userDetails;

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                        authenticationRequest.getPassword())
        );
        userDetails = (UserDetails) auth.getPrincipal();

        final String jwt = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }


}
