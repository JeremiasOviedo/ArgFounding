package com.nocountry.c930.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

  @NotEmpty(message = "Email must not be blank")
  @Email(message = "The email must be real email")
  private String email;

  @NotEmpty(message = "Password must not be blank")
  private String password;
}
