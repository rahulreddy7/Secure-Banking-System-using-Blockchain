package io.sbs.exception;

import java.util.List;

public class ErrorResponse {
	public ErrorResponse(String message, List<String> details) {
        super();
        this.message = message;
        this.details = details;
    }
  
    private String message;
    private List<String> details;
}
