package com.nocountry.c930.exception;

import org.springframework.security.core.AuthenticationException;

public class RepeatedUsername extends AuthenticationException {

  public RepeatedUsername(String error) {
    super(error);
  }
}
