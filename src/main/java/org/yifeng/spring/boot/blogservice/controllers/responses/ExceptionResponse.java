package org.yifeng.spring.boot.blogservice.controllers.responses;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExceptionResponse {

	@JsonProperty("timestamp")
	private Long timestamp;

	@JsonProperty("status")
	private Integer status;

	@JsonProperty("error")
	private String error;

	@JsonProperty("exception")
	private String exception;

	@JsonProperty("message")
	private String message;

	@JsonProperty("path")
	private String path;

	public ExceptionResponse(Exception exception, HttpStatus httpStatus, HttpServletRequest httpServletRequest) {
		this.timestamp = new Date().getTime();
		this.status = httpStatus.value();
		this.error = httpStatus.getReasonPhrase();
		this.exception = exception.getClass().getCanonicalName();
		this.message = exception.getMessage();
		this.path = httpServletRequest.getRequestURI();
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public Integer getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getException() {
		return exception;
	}

	public String getMessage() {
		return message;
	}

	public String getPath() {
		return path;
	}
}
