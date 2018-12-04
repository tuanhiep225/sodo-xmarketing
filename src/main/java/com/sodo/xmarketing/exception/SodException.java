package com.sodo.xmarketing.exception;

public class SodException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = 2423868742886445529L;

  private String errorCode;

  public SodException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  /**
   * @return the errorCode
   */
  public String getErrorCode() {
    return errorCode;
  }

}
