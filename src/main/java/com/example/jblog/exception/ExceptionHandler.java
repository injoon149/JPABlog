package com.example.jblog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

import com.example.jblog.dto.ResponseDTO;

@ControllerAdvice
@RestController
public class ExceptionHandler {
	@org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
	public ResponseDTO<String> globalExceptionHandler(Exception e){
		return new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
	}

}
