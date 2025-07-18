package com.spring.cinema.exception;


import com.spring.cinema.handler.BusinessErrorCodes;

public class InvalidEntityException extends BaseException {

  // Constructeurs de InvalidEntityException
  public InvalidEntityException(String message) {
    super(message);
  }

  public InvalidEntityException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidEntityException(String message, BusinessErrorCodes errorCode) {
    super(message, errorCode);
  }

  public InvalidEntityException(String message, Throwable cause, BusinessErrorCodes errorCode) {
    super(message, cause, errorCode);
  }
}

