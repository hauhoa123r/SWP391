package org.project.exception;

public class ErrorResponse extends RuntimeException {
  public ErrorResponse(String message) {
    super(message);
  }
}
