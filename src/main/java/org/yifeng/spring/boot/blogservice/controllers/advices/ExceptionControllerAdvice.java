package org.yifeng.spring.boot.blogservice.controllers.advices;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.yifeng.spring.boot.blogservice.controllers.responses.ExceptionResponse;

@RestControllerAdvice
public class ExceptionControllerAdvice {

	@ExceptionHandler(value = {
			HttpRequestMethodNotSupportedException.class,
			HttpMessageNotReadableException.class,
			HttpMediaTypeNotSupportedException.class,
			MethodArgumentNotValidException.class,
			MethodArgumentTypeMismatchException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponse adviseBadRequest(Exception e, HttpServletRequest httpServletRequest) {
		return new ExceptionResponse(e, HttpStatus.BAD_REQUEST, httpServletRequest);
	}

	@ExceptionHandler(value = { IllegalArgumentException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ExceptionResponse adviseNotFound(Exception e, HttpServletRequest httpServletRequest) {
		return new ExceptionResponse(e, HttpStatus.NOT_FOUND, httpServletRequest);
	}

	@ExceptionHandler(value = { Exception.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionResponse adviseInternalServerError(Exception e, HttpServletRequest httpServletRequest) {
		return new ExceptionResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, httpServletRequest);
	}
}
