package com.nocountry.c930.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class UserRegistrationDto {
    @NotEmpty(message = "First name must not be blank")
    private String firstName;
    @NotEmpty(message = "Last name must not be blank")
    private String lastName;
    @NotEmpty(message = "Email must not be blank")
    @Email(message = "The email must be real email")
    private String email;
    @NotEmpty(message = "Password must not be blank")
    @Size(min = 8)
    private String password;

    private MultipartFile image;

}
