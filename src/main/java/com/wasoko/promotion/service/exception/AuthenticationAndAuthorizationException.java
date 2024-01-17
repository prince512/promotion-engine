package com.wasoko.promotion.service.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class AuthenticationAndAuthorizationException extends SokoRuntimeException {

  @Serial
  private static final long serialVersionUID = -4387834237443309223L;

  public AuthenticationAndAuthorizationException(String message) {
    super(message);
  }
}